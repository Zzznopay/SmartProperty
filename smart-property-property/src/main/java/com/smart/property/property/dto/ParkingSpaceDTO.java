package com.smart.property.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 车位 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class ParkingSpaceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "小区ID不能为空")
    private Long communityId;

    @NotBlank(message = "车位编号不能为空")
    private String parkingNo;

    /** 车位类型：1 产权车位 2 租赁车位 */
    private Integer parkingType;

    private BigDecimal parkingArea;

    private Long ownerId;

    private Long tenantId;

    private Integer status;

    private BigDecimal salePrice;

    private LocalDate saleDate;

    private BigDecimal rentPrice;

    private LocalDate rentStartDate;

    private LocalDate rentEndDate;

    private String remark;
}