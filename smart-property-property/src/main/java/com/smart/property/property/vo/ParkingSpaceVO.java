package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 车位 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class ParkingSpaceVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private String parkingNo;
    private Integer parkingType;
    private BigDecimal parkingArea;
    private Long ownerId;
    private String ownerName;
    private Long tenantId;
    private String tenantName;
    private Integer status;
    private BigDecimal salePrice;
    private LocalDate saleDate;
    private BigDecimal rentPrice;
    private LocalDate rentStartDate;
    private LocalDate rentEndDate;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}