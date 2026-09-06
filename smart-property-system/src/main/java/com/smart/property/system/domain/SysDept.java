package com.smart.property.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门实体
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
@TableName("sys_dept")
public class SysDept implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long companyId;

    private Long parentId;

    private String deptName;

    private Integer sort;

    private String leader;

    private String phone;

    private String email;

    private Integer status;

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

    /** 子部门列表(非数据库字段) */
    @TableField(exist = false)
    private List<SysDept> children;
}
