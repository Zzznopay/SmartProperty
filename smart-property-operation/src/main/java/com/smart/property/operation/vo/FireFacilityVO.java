package com.smart.property.operation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 消防设施 VO
 *
 * @author zzz
 * @since 2026-07-30
 */
@Data
public class FireFacilityVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private Long buildingId;
    private String buildingName;
    private String facilityName;
    private Integer facilityType;
    private String facilityNo;
    private String location;
    private LocalDate installDate;
    private LocalDate expireDate;
    private Integer status;
    private LocalDate lastCheckDate;
    private LocalDate nextCheckDate;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}