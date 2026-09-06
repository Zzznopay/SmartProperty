package com.smart.property.system.service;

import com.smart.property.system.vo.PaymentStatisticsVO;

import java.util.List;

/**
 * 报表服务接口（System 服务中只承担可聚合的报表，跨服务查询通过 Feign）
 *
 * @author zzz
 * @since 2026-07-28
 */
public interface ReportService {
    /**
     * 按月统计物业费收款
     */
    List<PaymentStatisticsVO> paymentStatistics(Long companyId, String startMonth, String endMonth);
}
