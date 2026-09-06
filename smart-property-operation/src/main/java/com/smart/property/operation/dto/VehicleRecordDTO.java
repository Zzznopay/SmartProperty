package com.smart.property.operation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车辆进出 DTO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class VehicleRecordDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long communityId;

    @NotBlank(message = "车牌号不能为空")
    private String plateNo;

    private Integer vehicleType;

    /** 1 进入 2 离开 */
    private Integer recordType;

    private LocalDateTime recordTime;

    private String gateName;

    private String imageUrl;

    private Long parkingId;

    private Integer isTemporary;

    private BigDecimal feeAmount;

    private Integer payStatus;

    private String remark;
}