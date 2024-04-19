package com.stars.backend.mq.impl;

import com.stars.backend.mq.QueueMessageService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 队列消息服务实现
 * 用于发送具有延迟的消息到 RabbitMQ 队列。
 *
 * @author stars
 */
@Service
public class QueueMessageServiceImpl implements QueueMessageService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送具有延迟的消息到队列
     *
     * @param exchangeKey 交换机名称
     * @param routingKey  路由键
     * @param msg         消息对象
     * @param xdelay      延迟时间（毫秒）
     */
    @Override
    public void delayedSend(String exchangeKey, String routingKey, Object msg, int xdelay) {
        this.rabbitTemplate.convertAndSend(exchangeKey, routingKey, msg, message -> {
            message.getMessageProperties().setDelay(xdelay);
            return message;
        });
    }
}
