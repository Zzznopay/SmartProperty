package com.smart.property.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 物业公司实体
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
@TableName("sys_company")
public class SysCompany implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String companyName;

    private String companyCode;

    private String contactName;

    private String contactPhone;

    private String address;

    private String logo;

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

    /** 子公司列表：仅用于返回树形结构,不参与持久化 */
    @TableField(exist = false)
    private List<SysCompany> children = new ArrayList<>();
}
