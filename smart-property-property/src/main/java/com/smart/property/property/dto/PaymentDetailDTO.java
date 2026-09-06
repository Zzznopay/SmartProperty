package com.smart.property.property.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 缴费明细 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class PaymentDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "台帐ID不能为空")
    private Long ledgerId;

    private Long feeItemId;

    private String ledgerMonth;

    private BigDecimal amount;

    private BigDecimal actualAmount;

    private BigDecimal discountAmount;

    private BigDecimal lateFee;
}
