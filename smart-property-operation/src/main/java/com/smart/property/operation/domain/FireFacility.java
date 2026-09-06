package com.smart.property.operation.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 消防设施实体
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
@TableName("operation_fire_facility")
public class FireFacility implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long companyId;

    private Long communityId;

    private Long buildingId;

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
