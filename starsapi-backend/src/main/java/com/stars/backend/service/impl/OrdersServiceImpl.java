package com.stars.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stars.backend.common.ErrorCode;
import com.stars.backend.common.PageHelper;
import com.stars.backend.constant.config.DeLayConfig;
import com.stars.backend.exception.BusinessException;
import com.stars.backend.mapper.OrdersMapper;
import com.stars.backend.model.dto.orders.OrdersQueryRequest;
import com.stars.backend.model.enums.OrdersStatusEnum;
import com.stars.backend.mq.QueueMessageService;
import com.stars.backend.service.CardPayResultService;
import com.stars.backend.service.OrdersService;
import com.stars.common.model.entity.CardPayResult;
import com.stars.common.model.entity.Orders;
import com.stars.common.model.vo.OrdersVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
    private RedisTemplate redisTemplate;

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
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "校验失败，订单内部信息存在空白");
        }
        if (StringUtils.isAnyBlank(rechargeTimes) && rechargeTimes1 > 100L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "校验失败，充值次数过大");
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
        List<OrdersVO> ordersVOs = this.ordersMapper.selectMyOrders(userId, start, pageSize);
        int count = ordersVOs.size();
        int pageCount = (count % pageSize == 0) ? (count / pageSize) : (count / pageSize + 1);
        PageHelper<OrdersVO> ordersVOPageHelper = new PageHelper<>(count, pageCount, ordersVOs);
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
        QueryWrapper<Orders> ordersQueryWrapper = new QueryWrapper<>();
        ordersQueryWrapper.eq("interfId", id);
        ordersQueryWrapper.eq("userId", userId);
        ordersQueryWrapper.eq("status", status);
        ordersQueryWrapper.eq("createTime", createTime);
        Orders orders = this.getOne(ordersQueryWrapper);
        // 删除订单
        this.remove(ordersQueryWrapper);
        if (orders.getStatus() == OrdersStatusEnum.PAID.getValue() || orders.getStatus() == OrdersStatusEnum.SUCCESS.getValue()) {
            QueryWrapper<CardPayResult> payResultQueryWrapper = new QueryWrapper<>();
            payResultQueryWrapper.eq("interfId", id);
            payResultQueryWrapper.eq("userId", userId);
            // 删除结算
            this.cardPayResultService.remove(payResultQueryWrapper);
        }
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
        ordersQueryWrapper.eq("status", OrdersStatusEnum.WAIT.getValue());
        long count = this.count(ordersQueryWrapper);
        if (count != 0) {
            return false;
        }
        Orders orders = new Orders();
        orders.setUserId(userId);
        orders.setInterfId(interfId);
        orders.setRechargeTimes(num);
        this.save(orders);
        // 放入队列
        String msg = String.valueOf(orders.getId());
        int xdelay = 900000; // 15min
        this.queueMessageService.delayedSend(DeLayConfig.DIRECT_EXCHANGE_NAME_ORDER,
                DeLayConfig.DIRECT_EXCHANGE_ROUT_KEY_ORDER,
                msg,
                xdelay);
        return true;
    }
}
