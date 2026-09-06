package com.smart.property.operation.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 消防巡查实体
 *
 * @author zzz
 * @since 2026-07-28
 */
@Data
@TableName("operation_fire_patrol")
public class FirePatrol implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long companyId;

    private Long communityId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate patrolDate;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime patrolTime;

    private String patrolArea;

    private Integer patrolResult;

    private String problems;

    private Long patrolUserId;

    private String patrolUserName;

    private String handleContent;

    private Integer status;

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
