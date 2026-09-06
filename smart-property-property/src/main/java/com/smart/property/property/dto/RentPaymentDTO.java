package com.smart.property.property.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 租金 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class RentPaymentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "合同ID不能为空")
    private Long contractId;

    private Long tenantId;

    private Long roomId;

    private String paymentNo;

    private String rentMonth;

    @NotNull(message = "租金金额不能为空")
    private BigDecimal rentAmount;

    private BigDecimal actualAmount;

    private LocalDateTime payTime;

    private Integer payType;

    private Integer status;

    private String remark;
}