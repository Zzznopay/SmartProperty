package com.smart.property.system.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 缴费统计 VO
 *
 * @author zzz
 * @since 2026-07-28
 */
@Data
public class PaymentStatisticsVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 月份 yyyy-MM */
    private String month;

    /** 总收款额 */
    private BigDecimal totalAmount;

    /** 涉及的业主数 */
    private Integer ownerCount;
}
