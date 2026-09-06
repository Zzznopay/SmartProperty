package com.smart.property.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房间 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class RoomDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "小区ID不能为空")
    private Long communityId;

    @NotNull(message = "楼宇ID不能为空")
    private Long buildingId;

    @NotNull(message = "单元ID不能为空")
    private Long unitId;

    @Size(max = 64)
    private String roomCode;

    @NotBlank(message = "房号不能为空")
    private String roomNo;

    private Integer floor;

    /** 户型：1 一居 2 二居 3 三居 4 四居及以上 5 其他 */
    private Integer roomType;

    private BigDecimal buildArea;

    private BigDecimal innerArea;

    private BigDecimal publicArea;

    private String orientation;

    private Integer decoration;

    private Integer status;

    private Long ownerId;

    private Long tenantId;

    private LocalDateTime checkInTime;

    private String remark;
}