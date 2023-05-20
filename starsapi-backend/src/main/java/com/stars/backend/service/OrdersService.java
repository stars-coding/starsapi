package com.stars.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stars.backend.common.PageHelper;
import com.stars.backend.model.dto.orders.OrdersQueryRequest;
import com.stars.common.model.entity.Orders;
import com.stars.common.model.vo.OrdersVO;

/**
 * 订单服务接口
 * 提供订单相关操作的接口定义，包括验证订单信息、获取我的订单、删除订单及支付结果、创建订单等。
 *
 * @author stars
 */
public interface OrdersService extends IService<Orders> {

    /**
     * 验证订单信息
     *
     * @param orders 订单对象
     * @param add    是否新增订单
     */
    void validOrdersInfo(Orders orders, boolean add);

    /**
     * 获取我的订单
     *
     * @param ordersQueryRequest 订单查询请求对象
     * @return 我的订单列表
     */
    PageHelper<OrdersVO> getMyOrders(OrdersQueryRequest ordersQueryRequest);

    /**
     * 删除订单及支付结果
     *
     * @param id         订单ID
     * @param userId     用户ID
     * @param status     订单状态
     * @param createTime 订单创建时间
     * @return 是否成功删除订单及支付结果
     */
    boolean deleteOrdersAndPayResult(long id, long userId, int status, String createTime);

    /**
     * 创建订单
     *
     * @param userId   用户ID
     * @param interfId 接口ID
     * @param num      订单数量
     * @return 是否成功创建订单
     */
    boolean createOrders(long userId, long interfId, long num);
}
