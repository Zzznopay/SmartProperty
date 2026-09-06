package com.smart.property.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房间实体
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
@TableName("property_room")
public class Room implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long companyId;

    private Long communityId;

    private Long buildingId;

    private Long unitId;

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

    private Long tenantId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;

    private String remark;

    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
