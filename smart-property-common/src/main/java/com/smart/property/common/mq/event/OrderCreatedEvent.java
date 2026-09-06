package com.smart.property.common.mq.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 服务工单已创建事件
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 工单ID */
    private Long orderId;

    /** 工单编号 */
    private String orderNo;

    /** 租户ID */
    private Long companyId;

    /** 工单标题 */
    private String title;

    /** 操作人姓名 */
    private String operatorName;

    /** 发生时间 */
    private LocalDateTime occurredAt;
}
