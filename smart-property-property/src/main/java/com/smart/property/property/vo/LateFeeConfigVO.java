package com.smart.property.property.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 滞纳金配置 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class LateFeeConfigVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private Long communityId;
    private String communityName;
    private Long feeItemId;
    private String feeItemName;
    private Integer graceDays;
    private Integer rateType;
    private BigDecimal rate;
    private BigDecimal maxAmount;
    private Integer isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}