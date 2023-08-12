package com.stars.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stars.backend.common.ErrorCode;
import com.stars.backend.common.FormatUtils;
import com.stars.backend.common.PageHelper;
import com.stars.backend.exception.BusinessException;
import com.stars.backend.mapper.OrdersMapper;
import com.stars.backend.model.dto.orders.OrdersQueryRequest;
import com.stars.backend.mq.QueueMessageService;
import com.stars.backend.service.CardPayResultService;
import com.stars.backend.service.OrdersService;
import com.stars.common.model.entity.CardPayResult;
import com.stars.common.model.entity.Orders;
import com.stars.common.model.vo.OrdersVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.stars.backend.constant.config.DeLayConfig.DIRECT_EXCHANGE_NAME_ORDER;
import static com.stars.backend.constant.config.DeLayConfig.DIRECT_EXCHANGE_ROUT_KEY_ORDER;

/**
 * 订单服务实现
 * 提供订单相关操作的接口定义，包括验证订单信息、获取我的订单、删除订单及支付结果、创建订单等。
 *
 * @author stars
 */
@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CardPayResultService cardPayResultService;

    @Resource
    private QueueMessageService queueMessageService;

    /**
     * 验证订单信息
     *
     * @param orders 订单对象
     * @param add    是否新增订单
     */
    @Override
    public void validOrdersInfo(Orders orders, boolean add) {
        if (orders == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userId = orders.getUserId().toString();
        String interfId = orders.getInterfId().toString();
        String rechargeTimes = orders.getRechargeTimes().toString();
        long rechargeTimes1 = orders.getRechargeTimes();
        if (add && StringUtils.isAnyBlank(userId, interfId, rechargeTimes)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (StringUtils.isAnyBlank(rechargeTimes) && rechargeTimes1 > 100L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "次数过大");
        }
    }

    /**
     * 获取我的订单
     *
     * @param ordersQueryRequest 订单查询请求对象
     * @return 我的订单列表
     */
    @Override
    public PageHelper<OrdersVO> getMyOrders(OrdersQueryRequest ordersQueryRequest) {
        long userId = ordersQueryRequest.getUserId();
        int pageSize = ordersQueryRequest.getPageSize();
        int pageNum = ordersQueryRequest.getCurrent();
        int start = (pageNum - 1) * pageSize;
        String key = "cache:myOrders:" + userId;
        // 查看缓存是否存在
        List<String> ordersList = this.stringRedisTemplate.opsForList().range(key, 0L, -1L);
        List<OrdersVO> ordersVOList = new ArrayList<>(128);
        if (ordersList != null && ordersList.size() != 0) {
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new FormatUtils()).create();
            for (int k = 0; k < ordersList.size(); k++) {
                OrdersVO ordersVO = (OrdersVO) gson.fromJson(ordersList.get(k), OrdersVO.class);
                ordersVOList.add(ordersVO);
            }
            int count = ordersVOList.size();
            int pageCount = (count % pageSize == 0) ? (count / pageSize) : (count / pageSize + 1);
            return new PageHelper(count, pageCount, ordersVOList);
        }
        // 缓存不存在，直接查询数据库
        List<OrdersVO> ordersVOS = this.ordersMapper.selectMyOrders(userId, start, pageSize);
        // 封装数据到redis，使用list数据结构
        for (int i = 0; i < ordersVOS.size(); i++) {
            String ordersVOSJson = null;
            try {
                ordersVOSJson = (new ObjectMapper()).writeValueAsString(ordersVOS.get(i));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            this.stringRedisTemplate.opsForList().leftPush(key, ordersVOSJson);
            this.stringRedisTemplate.expire(key, 30L, TimeUnit.MINUTES);
        }
        int count = ordersVOS.size();
        int pageCount = (count % pageSize == 0) ? (count / pageSize) : (count / pageSize + 1);
        PageHelper<OrdersVO> ordersVOPageHelper = new PageHelper<>(count, pageCount, ordersVOS);
        return ordersVOPageHelper;
    }

    /**
     * 删除订单及支付结果
     *
     * @param id         订单ID
     * @param userId     用户ID
     * @param status     订单状态
     * @param createTime 订单创建时间
     * @return 是否成功删除订单及支付结果
     */
    @Override
    public boolean deleteOrdersAndPayResult(long id, long userId, int status, String createTime) {
        // todo 有多个 id 时删除失败
        QueryWrapper<Orders> ordersQueryWrapper = new QueryWrapper<>();
        ordersQueryWrapper.eq("interfId", id);
        ordersQueryWrapper.eq("userId", userId);
        ordersQueryWrapper.eq("status", status);
        ordersQueryWrapper.eq("createTime", createTime);
        Orders orders = getOne(ordersQueryWrapper);
        remove(ordersQueryWrapper);
        if (orders.getStatus() == 1 || orders.getStatus() == 2) {
            QueryWrapper<CardPayResult> payResultQueryWrapper = new QueryWrapper<>();
            payResultQueryWrapper.eq("interfId", id);
            payResultQueryWrapper.eq("userId", userId);
            this.cardPayResultService.remove(payResultQueryWrapper);
        }
        String key = "cache:myOrders:" + userId;
        this.stringRedisTemplate.delete(key);
        return true;
    }

    /**
     * 创建订单
     *
     * @param userId   用户ID
     * @param interfId 接口ID
     * @param num      订单数量
     * @return 是否成功创建订单
     */
    @Transactional
    @Override
    public boolean createOrders(long userId, long interfId, long num) {
        QueryWrapper<Orders> ordersQueryWrapper = new QueryWrapper<>();
        ordersQueryWrapper.eq("userId", userId);
        ordersQueryWrapper.eq("interfId", interfId);
        ordersQueryWrapper.eq("status", 0);
        long count = this.count(ordersQueryWrapper);
        if (count != 0) {
            return false;
        }
        Orders orders = new Orders();
        orders.setUserId(userId);
        orders.setInterfId(interfId);
        orders.setRechargeTimes(num);
        this.save(orders);
        String key = "cache:myOrders:" + userId;
        this.stringRedisTemplate.delete(key);
        String msg = String.valueOf(orders.getId());
        int xdelay = 900000; // 15min
        this.queueMessageService.delayedSend(DIRECT_EXCHANGE_NAME_ORDER, DIRECT_EXCHANGE_ROUT_KEY_ORDER, msg, xdelay);
        return true;
    }
}
