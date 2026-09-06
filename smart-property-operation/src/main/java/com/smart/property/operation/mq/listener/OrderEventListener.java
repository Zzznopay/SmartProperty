package com.smart.property.operation.mq.listener;

import com.smart.property.common.mq.constant.MqConstants;
import com.smart.property.common.mq.event.OrderCreatedEvent;
import com.smart.property.operation.dto.MessageDTO;
import com.smart.property.operation.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 服务工单事件监听器
 *
 * <p>工单创建后向相关工作人员发送系统内通知。
 * ReceiverId 暂取 0L（全员广播），生产环境可改为按部门/角色解析。</p>
 *
 * @author zzz
 * @since 2026-07-25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final MessageService messageService;

    @RabbitListener(queues = MqConstants.ORDER_CREATED_QUEUE)
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("收到订单已创建事件 orderId={} orderNo={}", event.getOrderId(), event.getOrderNo());
        if (event == null || event.getOrderId() == null) {
            log.warn("订单事件为空或缺少orderId，忽略");
            return;
        }

        MessageDTO dto = new MessageDTO();
        dto.setMessageType(1);             // 系统通知
        dto.setTitle("新工单：" + event.getTitle());
        dto.setContent(String.format(
                "工单[%s]已创建，操作人：%s，时间：%s",
                event.getOrderNo(),
                event.getOperatorName(),
                event.getOccurredAt() == null ? LocalDateTime.now() : event.getOccurredAt()
        ));
        dto.setReceiverId(0L);              // 全员广播
        dto.setBusinessType("service_order");
        dto.setBusinessId(event.getOrderId());

        messageService.sendMessage(dto, event.getCompanyId(), 0L, "system");
    }
}
