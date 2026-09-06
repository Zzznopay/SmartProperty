package com.smart.property.common.mq.util;

import com.smart.property.common.mq.constant.MqConstants;
import com.smart.property.common.mq.event.OrderCreatedEvent;
import com.smart.property.common.mq.event.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 异步消息通用发件器
 *
 * @author zzz
 * @since 2026-07-25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MqUtils {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发布服务工单已创建事件
     */
    public void sendOrderCreated(OrderCreatedEvent event) {
        send(MqConstants.ORDER_EXCHANGE, MqConstants.ORDER_CREATED_RK, event, "OrderCreated");
    }

    /**
     * 发布缴费完成事件
     */
    public void sendPaymentCompleted(PaymentCompletedEvent event) {
        send(MqConstants.PAYMENT_EXCHANGE, MqConstants.PAYMENT_COMPLETED_RK, event, "PaymentCompleted");
    }

    private void send(String exchange, String routingKey, Object payload, String eventName) {
        String messageId = UUID.randomUUID().toString();
        log.info("发布异步事件 {} -> exchange={} rk={} messageId={}", eventName, exchange, routingKey, messageId);
        rabbitTemplate.convertAndSend(exchange, routingKey, payload, message -> {
            message.getMessageProperties().setMessageId(messageId);
            message.getMessageProperties().setHeader("eventType", eventName);
            return message;
        });
    }
}
