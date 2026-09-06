package com.smart.property.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 装修记录实体
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
@TableName("property_decoration_record")
public class DecorationRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long companyId;

    private Long roomId;

    private Long ownerId;

    private LocalDate applyDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private String decorationCompany;

    private String contactName;

    private String contactPhone;

    private BigDecimal deposit;

    private Integer depositStatus;

    private Integer checkResult;

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
