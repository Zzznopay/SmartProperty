package com.smart.property.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 楼宇 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class BuildingDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "小区ID不能为空")
    private Long communityId;

    @NotBlank(message = "楼宇名称不能为空")
    @Size(max = 64, message = "楼宇名称长度不能超过64个字符")
    private String buildingName;

    @Size(max = 64, message = "楼宇编码长度不能超过64个字符")
    private String buildingCode;

    /** 楼宇类型：1 住宅 2 商业 3 写字楼 4 工业 */
    private Integer buildingType;

    private Integer floorCount;

    private Integer roomCount;

    private BigDecimal area;

    private Integer buildYear;

    private Integer status;

    private String remark;
}