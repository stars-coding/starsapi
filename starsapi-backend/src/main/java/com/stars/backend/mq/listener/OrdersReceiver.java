package com.stars.backend.mq.listener;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.stars.backend.constant.config.DeLayConfig;
import com.stars.backend.model.enums.OrdersStatusEnum;
import com.stars.backend.service.OrdersService;
import com.stars.common.model.entity.Orders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 订单消息接收器
 * 监听订单消息队列，处理订单相关消息，包括修改订单状态和缓存清除等操作。
 *
 * @author stars
 */
@Component
@Slf4j
@EnableRabbit
public class OrdersReceiver {

    @Resource
    private OrdersService ordersService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 监听订单消息队列
     *
     * @param msg 订单消息
     */
    @RabbitListener(queues = DeLayConfig.QUEUE_NAME_ORDER)
    public void listenOrdersQueue(String msg) {
        this.log.info("收到订单消息: {}", msg);
        try {
            // 获取订单
            long ordersId = Long.parseLong(msg);
            Orders orders = this.ordersService.getById(ordersId);
            // 订单待支付，将其标志未完成
            if (orders.getStatus() == 0) {
                UpdateWrapper<Orders> ordersUpdateWrapper = new UpdateWrapper<>();
                ordersUpdateWrapper.eq("id", ordersId);
                ordersUpdateWrapper.setSql("status = " + OrdersStatusEnum.FAIL.getValue());
                this.ordersService.update(ordersUpdateWrapper);
            }
        } catch (Exception e) {
            this.log.error("处理订单消息时发生异常: {}", e.getMessage(), e);
        }
    }
}
