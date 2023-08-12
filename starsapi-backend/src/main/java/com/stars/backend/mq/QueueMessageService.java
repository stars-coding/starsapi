package com.stars.backend.mq;

/**
 * 队列消息服务接口
 *
 * @author stars
 */
public interface QueueMessageService {

    /**
     * 延迟发送
     * 发送具有延迟的消息到队列
     *
     * @param exchangeKey 交换机名称
     * @param routingKey  路由键
     * @param msg         消息对象
     * @param xdelay      延迟时间（毫秒）
     */
    void delayedSend(String exchangeKey, String routingKey, Object msg, int xdelay);
}
