package com.smart.property.common.mq.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 缴费完成事件
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 缴费流水ID */
    private Long paymentId;

    /** 租户ID */
    private Long companyId;

    /** 业主ID */
    private Long ownerId;

    /** 业主姓名 */
    private String ownerName;

    /** 缴费金额（元） */
    private BigDecimal amount;

    /** 费用项名称 */
    private String feeItemName;

    /** 发生时间 */
    private LocalDateTime occurredAt;
}
