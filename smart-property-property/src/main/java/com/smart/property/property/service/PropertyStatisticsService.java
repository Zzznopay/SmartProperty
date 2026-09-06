package com.smart.property.property.service;

import com.smart.property.property.vo.PropertyOverviewVO;

import java.util.List;
import java.util.Map;

/**
 * 房产财务统计服务接口
 *
 * @author zzz
 * @since 2026-07-29
 */
public interface PropertyStatisticsService {

    /**
     * 统计概览：小区/楼栋/房间/业主/车位/生效合同/本月收费/待缴费账单
     */
    PropertyOverviewVO getOverview(Long companyId);

    /**
     * 近 N 月每月收费金额趋势
     *
     * @return [{@code month: "yyyy-MM", amount: BigDecimal}]
     */
    List<Map<String, Object>> getPaymentTrend(Long companyId, int months);

    /**
     * 账单状态分布
     *
     * @return [{@code status: Integer, count: Long}]
     */
    List<Map<String, Object>> getFeeStatusDistribution(Long companyId);
}
