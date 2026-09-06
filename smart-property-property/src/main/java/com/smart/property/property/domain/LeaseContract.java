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
 * 租赁合同实体
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
@TableName("property_lease_contract")
public class LeaseContract implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long companyId;

    private String contractNo;

    private Long roomId;

    private Long tenantId;

    private Integer leaseType;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal rentAmount;

    private BigDecimal deposit;

    private Integer payCycle;

    private Integer status;

    private LocalDate terminateDate;

    private String terminateReason;

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
