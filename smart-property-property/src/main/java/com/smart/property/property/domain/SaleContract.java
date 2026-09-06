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
 * 销售合同实体
 *
 * @author zzz
 * @since 2026-07-28
 */
@Data
@TableName("property_sale_contract")
public class SaleContract implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long companyId;

    private String contractNo;

    private Long roomId;

    private Long ownerId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate contractDate;

    private BigDecimal salePrice;

    /** 1全款 2按揭 3分期 */
    private Integer payType;

    private BigDecimal downPayment;

    private BigDecimal loanAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;

    /** 0未交付 1已交付 */
    private Integer deliveryStatus;

    /** 1草稿 2生效 3已作废 */
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
