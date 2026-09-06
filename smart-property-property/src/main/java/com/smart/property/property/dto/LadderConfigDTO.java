package com.smart.property.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 阶梯收费配置 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class LadderConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "阶梯名称不能为空")
    private String ladderName;

    private BigDecimal minValue;

    private BigDecimal maxValue;

    @NotNull(message = "单价不能为空")
    private BigDecimal unitPrice;

    private Integer sort;
}
