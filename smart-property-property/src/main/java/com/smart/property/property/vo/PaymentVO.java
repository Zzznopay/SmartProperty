package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收费记录 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class PaymentVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private String paymentNo;
    private Long roomId;
    private String roomNo;
    private Long ownerId;
    private String ownerName;
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
    private LocalDateTime createTime;

    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
