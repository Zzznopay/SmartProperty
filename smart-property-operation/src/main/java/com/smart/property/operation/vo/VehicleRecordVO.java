package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 车辆进出 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class VehicleRecordVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private String plateNo;
    private Integer vehicleType;
    private Integer recordType;
    private LocalDateTime recordTime;
    private String gateName;
    private String imageUrl;
    private Long parkingId;
    private Integer isTemporary;
    private BigDecimal feeAmount;
    private Integer payStatus;
    private LocalDateTime payTime;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}