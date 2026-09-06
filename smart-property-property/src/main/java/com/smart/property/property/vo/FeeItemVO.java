package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 费项 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class FeeItemVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private String feeName;
    private String feeCode;
    private Integer feeType;
    private Integer chargeMode;
    private BigDecimal unitPrice;
    private String unit;
    private Integer billingCycle;
    private Integer isLadder;
    private Integer isActive;
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}