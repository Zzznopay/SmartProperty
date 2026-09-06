package com.smart.property.property.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 滞纳金配置 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class LateFeeConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    @NotNull(message = "费项ID不能为空")
    private Long feeItemId;

    private Integer graceDays;

    /** 1 固定金额 2 百分比 */
    private Integer rateType;

    @NotNull(message = "费率不能为空")
    private BigDecimal rate;

    private BigDecimal maxAmount;

    private Integer isActive;
}