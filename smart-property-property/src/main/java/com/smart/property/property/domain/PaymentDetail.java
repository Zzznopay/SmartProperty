package com.smart.property.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 缴费明细实体（一笔 Payment 对应多笔 Ledger 的核销明细）
 *
 * @author zzz
 * @since 2026-07-28
 */
@Data
@TableName("finance_payment_detail")
public class PaymentDetail implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long companyId;

    private Long paymentId;

    private Long ledgerId;

    private Long feeItemId;

    private String ledgerMonth;

    private BigDecimal amount;

    private BigDecimal actualAmount;

    private BigDecimal discountAmount;

    private BigDecimal lateFee;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
