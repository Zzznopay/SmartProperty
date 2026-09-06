package com.smart.property.property.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 房产财务统计概览 VO
 *
 * @author zzz
 * @since 2026-07-29
 */
@Data
public class PropertyOverviewVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 小区数 */
    private Long communityCount;

    /** 楼栋数 */
    private Long buildingCount;

    /** 房间数 */
    private Long roomCount;

    /** 业主数 */
    private Long ownerCount;

    /** 车位数 */
    private Long parkingSpaceCount;

    /** 生效租赁合同数 */
    private Long activeLeaseCount;

    /** 本月收费金额 */
    private BigDecimal monthPaymentAmount;

    /** 待缴费账单数 */
    private Long pendingLedgerCount;
}
