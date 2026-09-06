package com.smart.property.system.service.impl;

import com.smart.property.system.remote.PaymentRemoteClient;
import com.smart.property.system.service.ReportService;
import com.smart.property.system.vo.PaymentStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 报表服务实现（通过 Feign 跨服务聚合 property 服务的数据）
 *
 * @author zzz
 * @since 2026-07-28
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentRemoteClient paymentRemoteClient;

    @Override
    public List<PaymentStatisticsVO> paymentStatistics(Long companyId, String startMonth, String endMonth) {
        // 实际生产可通过 OpenFeign 调用 property 服务的 finance_payment 聚合 SQL
        // 这里提供占位返回，避免运行时阻塞；接口已就绪等待 property 服务支付聚合的开放
        return Collections.emptyList();
    }
}
