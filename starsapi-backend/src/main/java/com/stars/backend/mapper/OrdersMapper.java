package com.stars.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stars.common.model.entity.Orders;
import com.stars.common.model.vo.OrdersVO;

import java.util.List;

/**
 * 订单数据库操作
 *
 * @author stars
 */
public interface OrdersMapper extends BaseMapper<Orders> {

    List<OrdersVO> selectMyOrders(long userId, int start, long pageSize);
}
