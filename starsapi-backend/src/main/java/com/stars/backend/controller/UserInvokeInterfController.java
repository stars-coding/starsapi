package com.stars.backend.controller;

import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stars.backend.annotation.AuthCheck;
import com.stars.backend.common.*;
import com.stars.backend.exception.BusinessException;
import com.stars.backend.model.dto.userinvokeinterf.UserInvokeInterfAddRequest;
import com.stars.backend.model.dto.userinvokeinterf.UserInvokeInterfQueryRequest;
import com.stars.backend.model.dto.userinvokeinterf.UserInvokeInterfUpdateRequest;
import com.stars.backend.service.*;
import com.stars.common.model.entity.*;
import com.stars.common.model.vo.SelfInterfDataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.stars.backend.constant.RedisConstants.CACHE_MY_ORDERS_KEY;
import static com.stars.backend.constant.RedisConstants.LOCK_PAY_ORDER_KEY;
import static com.stars.backend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户调用接口控制器
 *
 * @author stars
 */
@RestController
@Slf4j
@RequestMapping("/userInvokeInterf")
public class UserInvokeInterfController {

    @Resource
    private UserInvokeInterfService userInvokeInterfService;

    @Resource
    private InterfService interfService;

    @Resource
    private UserService userService;

    @Resource
    private OrdersService ordersService;

    @Resource
    private CardService cardService;

    @Resource
    private CardPayResultService cardPayResultService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 支付接口
     * 用户支付接口调用费用并增加调用次数。
     *
     * @param interfName   调用的接口名称
     * @param cardNumber   充值卡号
     * @param cardPassword 充值卡密码
     * @param payAccount   充值账户
     * @param num          增加的调用次数
     * @param request      请求对象
     * @return 包含支付结果的响应对象
     */
    @PostMapping("/payInterf")
    // 出现检查异常也应该回滚
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> payInterf(
            @RequestParam("interfName") String interfName,
            @RequestParam("cardNumber") String cardNumber,
            @RequestParam("cardPassword") String cardPassword,
            @RequestParam("payAccount") String payAccount,
            @RequestParam("num") long num,
            HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = this.userService.getLoginUser(request);
        // 构建缓存的键
        long id = loginUser.getId();
        String key = LOCK_PAY_ORDER_KEY + id;
        SimpleRedisLock lock = new SimpleRedisLock(this.stringRedisTemplate, key);
        // 尝试获取锁
        boolean isLock = lock.tryLock(2400L);
        if (!isLock) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                lock.unlock();
                e.printStackTrace();
            }
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "用户调用接口不存在");
        }
        // 查询接口
        LambdaQueryWrapper<Interf> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Interf::getInterfName, interfName);
        Interf interf = this.interfService.getOne(lqw);
        // 如果接口为空，表示接口不存在，则抛出异常
        if (interf == null) {
            lock.unlock();
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口不存在");
        }
        // 查询用户
        LambdaQueryWrapper<User> lqw1 = new LambdaQueryWrapper<>();
        lqw1.eq(User::getUserAccount, payAccount);
        User user = this.userService.getOne(lqw1);
        // 如果用户为空，表示用户不存在，则抛出异常
        if (user == null) {
            lock.unlock();
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
        }
        // 查询卡
        cardNumber = SecureUtil.md5(cardNumber);
        cardPassword = SecureUtil.md5(cardPassword);
        QueryWrapper<Card> cardQueryWrapper = new QueryWrapper<>();
        cardQueryWrapper.eq("cardNumber", cardNumber);
        cardQueryWrapper.eq("cardPassword", cardPassword);
        Card card = this.cardService.getOne(cardQueryWrapper);
        // 如果卡为空，表示不存在，则抛出异常
        if (card == null) {
            lock.unlock();
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "卡不存在");
        }
        // 卡正确，删除卡
        boolean remove = this.cardService.remove(cardQueryWrapper);
        // 如果删除失败，表示卡已失效，则抛出异常
        if (!remove) {
            lock.unlock();
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "卡已失效");
        }
        // 充值完，增加用户剩余调用次数
        long userId = user.getId();
        long interfId = interf.getId();
        LambdaQueryWrapper<UserInvokeInterf> lqw2 = new LambdaQueryWrapper<>();
        lqw2.eq(UserInvokeInterf::getUserId, userId);
        lqw2.eq(UserInvokeInterf::getInterfId, interfId);
        UserInvokeInterf one = this.userInvokeInterfService.getOne(lqw2);
        // 如果【用户调用接口】不为空，表示用户拥有此接口的调用权限，则增加剩余调用次数
        if (one != null) {
            one.setLeftInvokeNum(one.getLeftInvokeNum() + num);
            this.userInvokeInterfService.saveOrUpdate(one);
        } else {
            // 如果【用户调用接口】为空，表示用户没有此接口的调用权限，则创建【用户调用接口】，增加剩余调用次数
            UserInvokeInterf userInvokeInterf = new UserInvokeInterf();
            userInvokeInterf.setUserId(userId);
            userInvokeInterf.setInterfId(interfId);
            userInvokeInterf.setLeftInvokeNum(num);
            this.userInvokeInterfService.save(userInvokeInterf);
        }
        // 查询订单（待支付）
        QueryWrapper<Orders> ordersQueryWrapper = new QueryWrapper<>();
        ordersQueryWrapper.eq("userId", userId);
        ordersQueryWrapper.eq("interfId", interfId);
        ordersQueryWrapper.eq("status", 0);
        Orders orders = this.ordersService.getOne(ordersQueryWrapper);
        // 如果订单为空，表示订单不存在，则抛出异常
        if (orders == null) {
            lock.unlock();
            return ResultUtils.error(ErrorCode.NOT_FOUND_ERROR, "订单不存在");
        }
        // 保存支付结算
        CardPayResult payResult = new CardPayResult();
        payResult.setCardId(card.getId());
        payResult.setUserId(userId);
        payResult.setInterfId(interfId);
        payResult.setOrdersId(orders.getId());
        boolean save = this.cardPayResultService.save(payResult);
        // 如果支付结算保存失败，表示支付失败，则抛出异常
        if (!save) {
            lock.unlock();
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "支付失败");
        }
        // 获取订单的创建时间
        String createTime = orders.getCreateTime().toString();
        DateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String formattedDate = null;
        try {
            Date date = inputFormat.parse(createTime);
            formattedDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 更新订单（已完成）
        UpdateWrapper<Orders> ordersUpdateWrapper = new UpdateWrapper<>();
        ordersUpdateWrapper.eq("userId", userId);
        ordersUpdateWrapper.eq("interfId", interfId);
        ordersUpdateWrapper.eq("createTime", formattedDate);
        ordersUpdateWrapper.setSql("status = 2");
        boolean result = this.ordersService.update(ordersUpdateWrapper);
        // todo 更新成功是否需要删除缓存
        if (result) {
            String key1 = CACHE_MY_ORDERS_KEY + userId;
            this.stringRedisTemplate.delete(key1);
            lock.unlock();
            // 返回一个成功的响应，响应体中携带result值
            return ResultUtils.success(result);
        }
        lock.unlock();
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }

    /**
     * 添加用户调用接口
     * 添加用户拥有的接口。
     *
     * @param userInvokeInterfAddRequest 请求参数，包含用户调用接口
     * @param request                    请求对象
     * @return 包含新用户调用接口ID的响应对象
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> addUserInvokeInterf(
            @RequestBody UserInvokeInterfAddRequest userInvokeInterfAddRequest,
            HttpServletRequest request) {
        // 如果用户调用接口添加请求为空，则抛出异常
        if (userInvokeInterfAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户调用接口添加请求为空");
        }
        // 创建用户调用接口
        UserInvokeInterf userInvokeInterf = new UserInvokeInterf();
        // 属性赋值
        BeanUtils.copyProperties(userInvokeInterfAddRequest, userInvokeInterf);
        // 校验【用户调用接口】
        this.userInvokeInterfService.validUserInvokeInterf(userInvokeInterf, true);
        // 获取当前登录用户
        User loginUser = this.userService.getLoginUser(request);
        userInvokeInterf.setUserId(loginUser.getId());
        // 保存用户调用接口
        boolean result = this.userInvokeInterfService.save(userInvokeInterf);
        // 如果保存【用户调用接口】失败，则抛出异常
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存用户调用接口失败");
        }
        // 获取新创建的【用户调用接口】ID
        long newUserInvokeInterfId = userInvokeInterf.getId();
        // 返回一个成功的响应，响应体中携带newUserInvokeInterfId值
        return ResultUtils.success(newUserInvokeInterfId);
    }

    /**
     * 删除用户调用接口
     * 删除用户拥有的接口。
     *
     * @param deleteRequest 请求参数，包含待删除的用户接口ID
     * @param request       请求对象
     * @return 包含删除操作结果的响应对象
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteUserInvokeInterf(@RequestBody DeleteRequest deleteRequest,
                                                        HttpServletRequest request) {
        // 如果删除请求为空，则抛出异常
        if (deleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除请求为空");
        }
        // 如果删除请求中的ID小于等于零，则抛出异常
        if (deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除请求中的ID小于等于零");
        }
        // 获取当前登录用户
        User user = this.userService.getLoginUser(request);
        // 获取待删除的【用户调用接口】ID
        long id = deleteRequest.getId();
        UserInvokeInterf oldUserInvokeInterf = this.userInvokeInterfService.getById(id);
        // 如果【用户调用接口】为空，表示【用户调用接口】不存在，则抛出异常
        if (oldUserInvokeInterf == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户调用接口不存在");
        }
        // 检查权限，只有接口创建者或管理员才有权限
        if (!oldUserInvokeInterf.getUserId().equals(user.getId()) && !this.userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "暂无权限删除用户调用接口");
        }
        // 删除【用户调用接口】
        boolean result = this.userInvokeInterfService.removeById(id);
        // 返回一个成功的响应，响应体中携带result值
        return ResultUtils.success(result);
    }

    /**
     * 更新用户调用接口
     * 更新用户拥有的接口。
     *
     * @param userInvokeInterfUpdateRequest 请求参数，包含用户调用接口的更新内容
     * @param request                       请求对象
     * @return 包含更新操作结果的响应对象
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateUserInvokeInterf(@RequestBody UserInvokeInterfUpdateRequest userInvokeInterfUpdateRequest,
                                                        HttpServletRequest request) {
        // 如果用户调用接口更新请求为空，则抛出异常
        if (userInvokeInterfUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户调用接口更新请求为空");
        }
        // 如果用户调用接口更新请求中的ID小于等于零，则抛出异常
        if (userInvokeInterfUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户调用接口更新请求中的ID小于等于零");
        }
        // 创建【用户调用接口】
        UserInvokeInterf userInvokeInterf = new UserInvokeInterf();
        // 属性赋值
        BeanUtils.copyProperties(userInvokeInterfUpdateRequest, userInvokeInterf);
        // 校验【用户调用接口】
        this.userInvokeInterfService.validUserInvokeInterf(userInvokeInterf, false);
        // 获取当前登录用户
        User user = this.userService.getLoginUser(request);
        long id = userInvokeInterfUpdateRequest.getId();
        // 获取【用户调用接口】
        UserInvokeInterf oldUserInvokeInterf = this.userInvokeInterfService.getById(id);
        // 如果【用户调用接口】为空，表示【用户调用接口】不存在，则抛出异常
        if (oldUserInvokeInterf == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户调用接口不存在");
        }
        // 检查权限，只有接口创建者或管理员才有权限
        if (!oldUserInvokeInterf.getUserId().equals(user.getId()) && !this.userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "暂无权限更新用户调用接口");
        }
        // 更新【用户调用接口】
        boolean result = this.userInvokeInterfService.updateById(userInvokeInterf);
        // 返回一个成功的响应，响应体中携带result值
        return ResultUtils.success(result);
    }

    /**
     * 根据ID获取用户调用接口
     *
     * @param id 用户接口信息ID
     * @return 包含用户接口信息的响应对象
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<UserInvokeInterf> getUserInvokeInterfById(long id) {
        // 如果ID小于等于零，则抛出异常
        if (id <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "ID小于等于零");
        }
        UserInvokeInterf userInvokeInterf = this.userInvokeInterfService.getById(id);
        // 返回一个成功的响应，响应体中携带userInvokeInterf信息
        return ResultUtils.success(userInvokeInterf);
    }

    /**
     * 获取用户调用接口列表
     *
     * @param userInvokeInterfQueryRequest 查询条件
     * @return 包含用户接口信息列表的响应对象
     */
    @AuthCheck(mustRole = "admin")
    @GetMapping("/list")
    public BaseResponse<List<UserInvokeInterf>> listUserInvokeInterf(UserInvokeInterfQueryRequest userInvokeInterfQueryRequest) {
        // 创建【用户调用接口】
        UserInvokeInterf userInvokeInterfQuery = new UserInvokeInterf();
        // 如果【用户调用接口】不为空，则属性赋值
        if (userInvokeInterfQueryRequest != null) {
            BeanUtils.copyProperties(userInvokeInterfQueryRequest, userInvokeInterfQuery);
        }
        // 构建查询条件
        QueryWrapper<UserInvokeInterf> queryWrapper = new QueryWrapper<>(userInvokeInterfQuery);
        // 查询
        List<UserInvokeInterf> userInvokeInterfList = this.userInvokeInterfService.list(queryWrapper);
        // 返回一个成功的响应，响应体中携带userInvokeInterfList信息
        return ResultUtils.success(userInvokeInterfList);
    }

    /**
     * 分页获取用户调用接口列表
     *
     * @param userInvokeInterfQueryRequest 查询条件
     * @param request                      请求对象
     * @return 包含用户接口信息分页数据的响应对象
     */
    @GetMapping("/list/page")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<UserInvokeInterf>> listUserInvokeInterfByPage(UserInvokeInterfQueryRequest userInvokeInterfQueryRequest,
                                                                           HttpServletRequest request) {
        // 如果用户调用接口查询请求为空，则抛出异常
        if (userInvokeInterfQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户调用接口查询请求为空");
        }
        UserInvokeInterf userInvokeInterfQuery = new UserInvokeInterf();
        BeanUtils.copyProperties(userInvokeInterfQueryRequest, userInvokeInterfQuery);
        long current = userInvokeInterfQueryRequest.getCurrent();
        long size = userInvokeInterfQueryRequest.getPageSize();
        String sortField = userInvokeInterfQueryRequest.getSortField();
        String sortOrder = userInvokeInterfQueryRequest.getSortOrder();
        if (size > 50L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserInvokeInterf> queryWrapper = new QueryWrapper<>(userInvokeInterfQuery);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals("ascend"), sortField);
        Page<UserInvokeInterf> userInvokeInterfPage = this.userInvokeInterfService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(userInvokeInterfPage);
    }

    /**
     * 获取用户拥有的接口视图列表
     * 属于脱敏获取。
     *
     * @param request 请求对象
     * @return 包含用户接口剩余调用次数的响应对象
     */
    @GetMapping("/selfInterfData")
    public BaseResponse<List<SelfInterfDataVO>> selfInterfData(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        long id = currentUser.getId();
        LambdaQueryWrapper<UserInvokeInterf> lqw = new LambdaQueryWrapper<>();
        lqw.eq(UserInvokeInterf::getUserId, id);
        List<UserInvokeInterf> list = this.userInvokeInterfService.list(lqw);
        List<SelfInterfDataVO> selfInterfDataVOs = new ArrayList<>();
        for (UserInvokeInterf userInvokeInterf : list) {
            SelfInterfDataVO selfInterfDataVO = new SelfInterfDataVO();
            BeanUtils.copyProperties(userInvokeInterf, selfInterfDataVO);
            long interfId = userInvokeInterf.getInterfId();
            LambdaQueryWrapper<Interf> lqw1 = new LambdaQueryWrapper<>();
            lqw1.eq(Interf::getId, interfId);
            Interf one = this.interfService.getOne(lqw1);
            String name = one.getInterfName();
            selfInterfDataVO.setInterfName(name);
            selfInterfDataVOs.add(selfInterfDataVO);
        }
        return ResultUtils.success(selfInterfDataVOs);
    }
}
