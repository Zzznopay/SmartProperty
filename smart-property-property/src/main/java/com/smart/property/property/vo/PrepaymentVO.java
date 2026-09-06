package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预收款 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class PrepaymentVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long ownerId;
    private String ownerName;
    private BigDecimal amount;
    private BigDecimal usedAmount;
    private BigDecimal balance;
    private LocalDateTime payTime;
    private Integer payType;
    private String paymentNo;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}