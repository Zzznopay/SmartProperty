package com.smart.property.common.mq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.smart.property.common.mq.constant.MqConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 拓扑与序列化配置
 *
 * <p>声明 Property 服务和 Operation 服务共用的交换机、队列及 JSON 消息转换器。
 * 业务服务只需依赖此模块即可获得开箱即用的发件能力。</p>
 *
 * @author zzz
 * @since 2026-07-25
 */
@Configuration
public class RabbitMqConfig {

    // ---------------- 工单事件 ----------------

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(MqConstants.ORDER_EXCHANGE, true, false);
    }

    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(MqConstants.ORDER_CREATED_QUEUE, true);
    }

    @Bean
    public Binding orderCreatedBinding(Queue orderCreatedQueue, TopicExchange orderExchange) {
        return BindingBuilder.bind(orderCreatedQueue).to(orderExchange).with(MqConstants.ORDER_CREATED_RK);
    }

    // ---------------- 缴费事件 ----------------

    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(MqConstants.PAYMENT_EXCHANGE, true, false);
    }

    @Bean
    public Queue paymentCompletedQueue() {
        return new Queue(MqConstants.PAYMENT_COMPLETED_QUEUE, true);
    }

    @Bean
    public Binding paymentCompletedBinding(Queue paymentCompletedQueue, TopicExchange paymentExchange) {
        return BindingBuilder.bind(paymentCompletedQueue).to(paymentExchange).with(MqConstants.PAYMENT_COMPLETED_RK);
    }

    // ---------------- 通用发件器 ----------------

    @Bean
    public ObjectMapper rabbitObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om;
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper rabbitObjectMapper) {
        return new Jackson2JsonMessageConverter(rabbitObjectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
