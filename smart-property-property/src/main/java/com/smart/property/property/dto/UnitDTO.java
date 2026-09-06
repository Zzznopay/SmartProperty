package com.smart.property.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 单元 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class UnitDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "楼宇ID不能为空")
    private Long buildingId;

    @NotBlank(message = "单元名称不能为空")
    private String unitName;

    private String unitCode;

    private Integer floorCount;

    private Integer roomCount;

    private Integer sort;

    private Integer status;
}