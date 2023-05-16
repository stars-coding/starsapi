package com.stars.backend.constant.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息队列延迟配置
 * 用于配置RabbitMQ的消息队列及其延迟特性。
 * 此配置包括创建一个Direct类型的交换机(exchange)、一个队列(queue)以及它们之间的绑定关系。
 * 注：这里的配置示例是用于RabbitMQ实现消息队列的延迟投递功能，确保消息在指定时间后才被消费。
 *
 * @author stars
 */
@Configuration
public class DeLayConfig {

    public static final String QUEUE_NAME_ORDER = "rabbitmq_queue_orders";

    public static final String DIRECT_EXCHANGE_NAME_ORDER = "rabbitmq_direct_exchange_orders";

    public static final String DIRECT_EXCHANGE_ROUT_KEY_ORDER = "rabbitmq.spring.boot.orders";

    /**
     * 创建一个延迟消息的Direct交换机
     *
     * @return 创建的Direct交换机对象
     */
    @Bean
    public DirectExchange directExchange() {
        return ExchangeBuilder
                .directExchange(DIRECT_EXCHANGE_NAME_ORDER)
                .durable(true)
                .delayed()
                .build();
    }

    /**
     * 创建一个消息队列
     *
     * @return 创建的消息队列对象
     */
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME_ORDER);
    }

    /**
     * 绑定消息队列和交换机
     *
     * @return 创建的绑定关系对象
     */
    @Bean
    public Binding bindingDirctExchangeAndQueue() {
        return BindingBuilder.bind(queue()).to(directExchange()).with(DIRECT_EXCHANGE_ROUT_KEY_ORDER);
    }
}
