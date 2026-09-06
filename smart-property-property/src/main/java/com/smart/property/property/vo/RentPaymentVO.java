package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 租金 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class RentPaymentVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long contractId;
    private String contractNo;
    private Long tenantId;
    private String tenantName;
    private Long roomId;
    private String roomNo;
    private String paymentNo;
    private String rentMonth;
    private BigDecimal rentAmount;
    private BigDecimal actualAmount;
    private LocalDateTime payTime;
    private Integer payType;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}