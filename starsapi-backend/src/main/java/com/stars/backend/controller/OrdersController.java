package com.stars.backend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.stars.backend.common.*;
import com.stars.backend.exception.BusinessException;
import com.stars.backend.model.dto.orders.OrdersDeleteRequest;
import com.stars.backend.model.dto.orders.OrdersQueryRequest;
import com.stars.backend.mq.QueueMessageService;
import com.stars.backend.service.InterfService;
import com.stars.backend.service.OrdersService;
import com.stars.backend.service.UserService;
import com.stars.common.model.entity.Interf;
import com.stars.common.model.entity.Orders;
import com.stars.common.model.entity.User;
import com.stars.common.model.vo.OrdersVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.stars.backend.constant.RedisConstants.CACHE_MY_ORDERS_KEY;
import static com.stars.backend.constant.RedisConstants.LOCK_ADD_ORDER_KEY;

/**
 * 订单控制器
 *
 * @author stars
 */
@RestController
@RequestMapping("/orders")
@Slf4j
public class OrdersController {

    /*
     * 线程池
     */
    private static final ExecutorService ORDER_EXECUTOR = Executors.newSingleThreadExecutor();
    /*
     * 阻塞队列
     */
    private BlockingQueue<Orders> ordersTasks = new ArrayBlockingQueue<>(1024 * 1024);
    @Resource
    private OrdersService ordersService;
    @Resource
    private InterfService interfService;
    @Resource
    private UserService userService;
    @Resource
    private QueueMessageService queueMessageService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 初始化
     * 构造函数之后立刻执行，启动了一个单线程的线程池ORDER_EXECUTOR，并提交一个异步任务OrdersHandler。
     */
    @PostConstruct
    private void init() {
        ORDER_EXECUTOR.submit(new OrdersHandler());
    }

    /**
     * 添加订单
     * 异步下单操作
     *
     * @param interfName         接口名称
     * @param payAccount         支付账户
     * @param num                充值次数
     * @param httpServletRequest 请求对象
     * @return 包含操作结果的响应对象
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addOrders(@RequestParam String interfName,
                                           @RequestParam String payAccount,
                                           @RequestParam long num,
                                           HttpServletRequest httpServletRequest) {
        // 获取当前登录用户
        User loginUser = this.userService.getLoginUser(httpServletRequest);
        // 查询接口
        QueryWrapper<Interf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interfName", interfName);
        Interf interf = this.interfService.getOne(queryWrapper);
        // 如果接口为空，表示不存在，则抛出异常
        if (interf == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        // 查询用户
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", payAccount);
        User user = this.userService.getOne(userQueryWrapper);
        // 如果用户为空，表示不存在，则抛出异常
        if (user == null) {
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        // 如果次数大于二百，则抛出异常
        if (num > 200L) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "次数大于二百");
        }
        // 创建订单
        Orders orders = new Orders();
        orders.setInterfId(interf.getId());
        orders.setUserId(user.getId());
        orders.setRechargeTimes(num);
        // 将订单添加至队列，异步处理
        boolean result = this.ordersTasks.add(orders);
        // todo 这里是否需要添加Redis缓存
        long userId = user.getId();
        String key = CACHE_MY_ORDERS_KEY + userId;
        // 将订单信息转换为JSON格式
        Gson gson = new Gson();
        String value = gson.toJson(orders);
        this.stringRedisTemplate.opsForValue().set(key, value);
        // 返回一个成功的响应，响应体中携带result值
        return ResultUtils.success(result);
    }

    /**
     * 分页获取当前用户的订单列表
     *
     * @param ordersQueryRequest 包含查询条件的请求对象
     * @param request            请求对象
     * @return 分页订单列表的响应对象
     */
    @GetMapping("/list/page")
    public BaseResponse<PageHelper<OrdersVO>> listMyOrdersByPage(OrdersQueryRequest ordersQueryRequest,
                                                                 HttpServletRequest request) {
        // 如果订单查询请求为空，则抛出异常
        if (ordersQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单查询请求为空");
        }
        // 获取当前登录用户的ID
        long id = this.userService.getLoginUser(request).getId();
        ordersQueryRequest.setUserId(id);
        // 获取分页订单列表
        PageHelper<OrdersVO> myOrdersList = this.ordersService.getMyOrders(ordersQueryRequest);
        // 返回一个成功的响应，响应体中携带myOrdersList信息
        return ResultUtils.success(myOrdersList);
    }

    /**
     * 删除订单和支付结果
     *
     * @param ordersDeleteRequest 包含订单ID和状态的请求对象
     * @param httpServletRequest  请求对象
     * @return 删除操作结果的响应对象
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteOrdersAndPayResult(@RequestBody OrdersDeleteRequest ordersDeleteRequest,
                                                          HttpServletRequest httpServletRequest) {
        // 如果订单删除请求为空，则抛出异常
        if (ordersDeleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单删除请求为空");
        }
        // 如果订单删除请求中的ID小于等于零，则抛出异常
        if (ordersDeleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "订单删除请求中的ID小于等于零");
        }
        // 获取日期
        String dateString = ordersDeleteRequest.getCreateTime().toString();
        // 格式化日期字符串
        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        // 解析日期
        Date date = null;
        try {
            date = inputFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "日期格式解析错误");
        }
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = outputFormat.format(date);
        // 获取当前登录用户
        User loginUser = this.userService.getLoginUser(httpServletRequest);
        long userId = loginUser.getId();
        long ordersId = ordersDeleteRequest.getId();
        int status = ordersDeleteRequest.getStatus();
        // 删除订单和支付结果
        boolean result = this.ordersService.deleteOrdersAndPayResult(ordersId, userId, status, createTime);
        // 返回一个成功的响应，响应体中携带myOrdersList信息
        return ResultUtils.success(result);
    }

    /**
     * 订单处理内部类
     * 用于处理订单的异步任务。
     *
     * @author stars
     */
    private class OrdersHandler implements Runnable {

        @Override
        public void run() {
            while (true) {
                Orders orders = null;
                try {
                    // 从订单任务队列中获取订单
                    orders = ordersTasks.take();
                    long userId = orders.getUserId();
                    long interfId = orders.getInterfId();
                    long num = orders.getRechargeTimes();
                    // 处理订单
                    this.handleOrders(userId, interfId, num);
                } catch (InterruptedException e) {
                    log.error("订单创建异常");
                    e.printStackTrace();
                }
            }
        }

        /**
         * 处理订单
         *
         * @param userId   用户ID
         * @param interfId 接口ID
         * @param num      充值次数
         */
        private void handleOrders(long userId, long interfId, long num) {
            // 构建缓存的键
            String key = LOCK_ADD_ORDER_KEY + userId;
            SimpleRedisLock lock = new SimpleRedisLock(stringRedisTemplate, key);
            // 尝试获取锁
            boolean isLock = lock.tryLock(1200L);
            if (!isLock) {
                log.error("处理订单失败");
                return;
            }
            // 创建订单
            ordersService.createOrders(userId, interfId, num);
            lock.unlock();
        }
    }
}
