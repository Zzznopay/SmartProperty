package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 台账 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class LedgerVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private Long roomId;
    private String roomNo;
    private Long ownerId;
    private String ownerName;
    private Long feeItemId;
    private String feeItemName;
    private String ledgerMonth;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal discountAmount;
    private BigDecimal lateFee;
    private Integer status;
    private LocalDate dueDate;
    private LocalDateTime payTime;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}