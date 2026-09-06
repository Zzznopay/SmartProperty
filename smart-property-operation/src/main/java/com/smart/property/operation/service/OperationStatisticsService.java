package com.smart.property.operation.service;

import com.smart.property.operation.vo.OperationOverviewVO;

import java.util.List;
import java.util.Map;

/**
 * 运营管理统计服务接口
 *
 * @author zzz
 * @since 2026-07-29
 */
public interface OperationStatisticsService {

    /**
     * 统计概览：工单/今日来访/今日车辆/公告/活动/各类巡查
     */
    OperationOverviewVO getOverview(Long companyId);

    /**
     * 工单状态分布
     *
     * @return [{@code status: Integer, count: Long}]
     */
    List<Map<String, Object>> getServiceOrderStatusDistribution(Long companyId);

    /**
     * 近 N 月每月工单量趋势
     *
     * @return [{@code month: "yyyy-MM", count: Long}]
     */
    List<Map<String, Object>> getServiceOrderTrend(Long companyId, int months);
}
