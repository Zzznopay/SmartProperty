package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房间 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class RoomVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private Long buildingId;
    private String buildingName;
    private Long unitId;
    private String unitName;
    private String roomCode;
    private String roomNo;
    private Integer floor;
    private Integer roomType;
    private BigDecimal buildArea;
    private BigDecimal innerArea;
    private BigDecimal publicArea;
    private String orientation;
    private Integer decoration;
    private Integer status;
    private Long ownerId;
    private String ownerName;
    private Long tenantId;
    private String tenantName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}