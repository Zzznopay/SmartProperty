package com.smart.property.property.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 小区 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class CommunityDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "小区名称不能为空")
    private String communityName;

    private String communityCode;

    private String address;

    private BigDecimal area;

    private Integer buildingCount;

    private Integer roomCount;

    private BigDecimal propertyFee;

    private String contactName;

    private String contactPhone;

    private Integer status;

    private String remark;
}