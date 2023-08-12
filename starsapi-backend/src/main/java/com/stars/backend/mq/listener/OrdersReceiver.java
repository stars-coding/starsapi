package com.stars.backend.mq.listener;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.stars.backend.constant.config.DeLayConfig;
import com.stars.backend.service.OrdersService;
import com.stars.common.model.entity.Orders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.stars.backend.constant.RedisConstants.CACHE_MY_ORDERS_KEY;

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
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 监听订单消息队列
     *
     * @param msg 订单消息
     */
    @RabbitListener(queues = DeLayConfig.QUEUE_NAME_ORDER)
    public void listenOrdersQueue(String msg) {
        this.log.info("收到订单消息: {}", msg);
        long ordersId = Long.parseLong(msg);
        try {
            UpdateWrapper<Orders> ordersUpdateWrapper = new UpdateWrapper<>();
            ordersUpdateWrapper.eq("id", ordersId);
            ordersUpdateWrapper.setSql("status = 3");
            Orders orders = this.ordersService.getById(ordersId);
            long userId = orders.getUserId();
            String key = CACHE_MY_ORDERS_KEY + userId;
            this.stringRedisTemplate.delete(key);
            // 需要判断订单是否未支付，未支付则修改订单状态
            int status = orders.getStatus();
            if (status == 0) {
                this.ordersService.update(ordersUpdateWrapper);
            }
        } catch (Exception e) {
            this.log.error("处理订单消息时发生异常: {}", e.getMessage(), e);
        }
    }
}
