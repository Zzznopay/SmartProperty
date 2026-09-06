package com.smart.property.operation.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 业委会成员实体
 *
 * @author zzz
 * @since 2026-07-28
 */
@Data
@TableName("admin_committee_member")
public class CommitteeMember implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long companyId;

    private Long communityId;

    private String memberName;

    private String position;

    private String phone;

    private Long roomId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate termStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate termEnd;

    private String photo;

    private String introduction;

    /** 1在任 2已离任 */
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
