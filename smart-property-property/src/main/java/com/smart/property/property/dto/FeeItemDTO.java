package com.smart.property.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 费项 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class FeeItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "小区ID不能为空")
    private Long communityId;

    @NotBlank(message = "费项名称不能为空")
    private String feeName;

    @NotBlank(message = "费项编码不能为空")
    private String feeCode;

    private Integer feeType;

    private Integer chargeMode;

    private BigDecimal unitPrice;

    private String unit;

    private Integer billingCycle;

    private Integer isLadder;

    private Integer isActive;

    private String remark;
}