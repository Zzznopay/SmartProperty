package com.smart.property.property.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 台账 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class LedgerDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "小区ID不能为空")
    private Long communityId;

    @NotNull(message = "房间ID不能为空")
    private Long roomId;

    private Long ownerId;

    @NotNull(message = "费项ID不能为空")
    private Long feeItemId;

    /** yyyy-MM */
    @NotNull(message = "账期月份不能为空")
    private String ledgerMonth;

    @NotNull(message = "应收金额不能为空")
    private BigDecimal amount;

    private BigDecimal paidAmount;

    private BigDecimal discountAmount;

    private BigDecimal lateFee;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    private String remark;
}