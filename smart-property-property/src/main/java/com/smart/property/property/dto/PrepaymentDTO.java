package com.smart.property.property.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预收款 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class PrepaymentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "业主ID不能为空")
    private Long ownerId;

    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    private BigDecimal usedAmount;

    private BigDecimal balance;

    private LocalDateTime payTime;

    private Integer payType;

    private String paymentNo;

    private Integer status;

    private String remark;
}