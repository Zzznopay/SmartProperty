package com.smart.property.operation.mq.listener;

import com.smart.property.common.mq.constant.MqConstants;
import com.smart.property.common.mq.event.PaymentCompletedEvent;
import com.smart.property.operation.dto.MessageDTO;
import com.smart.property.operation.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 缴费完成事件监听器
 *
 * <p>向业主发送缴费成功通知。ownerId 即 receiverId，支持个人消息回执。</p>
 *
 * @author zzz
 * @since 2026-07-25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final MessageService messageService;

    @RabbitListener(queues = MqConstants.PAYMENT_COMPLETED_QUEUE)
    public void onPaymentCompleted(PaymentCompletedEvent event) {
        log.info("收到缴费完成事件 paymentId={} ownerId={}", event.getPaymentId(), event.getOwnerId());
        if (event == null || event.getPaymentId() == null) {
            log.warn("缴费事件为空或缺少paymentId，忽略");
            return;
        }

        MessageDTO dto = new MessageDTO();
        dto.setMessageType(2);             // 缴费通知
        dto.setTitle("缴费成功");
        dto.setContent(String.format(
                "您已成功缴费 ¥%s，缴费时间：%s",
                event.getAmount(),
                event.getOccurredAt() == null ? LocalDateTime.now() : event.getOccurredAt()
        ));
        dto.setReceiverId(event.getOwnerId());
        dto.setReceiverName(event.getOwnerName());
        dto.setBusinessType("payment");
        dto.setBusinessId(event.getPaymentId());

        messageService.sendMessage(dto, event.getCompanyId(), 0L, "system");
    }
}
