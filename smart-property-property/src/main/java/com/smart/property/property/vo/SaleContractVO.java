package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售合同 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class SaleContractVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private String contractNo;
    private Long roomId;
    private String roomNo;
    private Long ownerId;
    private String ownerName;
    private LocalDate contractDate;
    private BigDecimal salePrice;
    private Integer payType;
    private BigDecimal downPayment;
    private BigDecimal loanAmount;
    private LocalDate deliveryDate;
    private Integer deliveryStatus;
    private Integer status;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}