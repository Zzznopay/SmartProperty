package com.smart.property.property.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收费记录实体
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
@TableName("finance_payment")
public class Payment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long companyId;

    private String paymentNo;

    private Long roomId;

    private Long ownerId;

    private BigDecimal totalAmount;

    private BigDecimal actualAmount;

    private BigDecimal discountAmount;

    private Integer payType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    private String receiptNo;

    private String invoiceNo;

    private Long cashierId;

    private String cashierName;

    private Integer status;

    private Integer auditStatus;

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
