package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 缴费明细 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class PaymentDetailVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
    private LocalDateTime createTime;
}
