package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.Result;
import com.smart.property.operation.service.OperationStatisticsService;
import com.smart.property.operation.vo.OperationOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 运营管理统计控制器
 *
 * @author zzz
 * @since 2026-07-29
 */
@RestController
@RequestMapping("/api/v1/operation/statistics")
@Tag(name = "运营统计")
@RequiredArgsConstructor
public class OperationStatisticsController {

    private final OperationStatisticsService operationStatisticsService;

    @GetMapping("/overview")
    public Result<OperationOverviewVO> overview() {
        return Result.success(operationStatisticsService.getOverview(SecurityContextHolder.getCompanyId()));
    }

    @GetMapping("/service-order-status")
    public Result<List<Map<String, Object>>> serviceOrderStatus() {
        return Result.success(operationStatisticsService.getServiceOrderStatusDistribution(
                SecurityContextHolder.getCompanyId()));
    }

    @GetMapping("/service-order-trend")
    public Result<List<Map<String, Object>>> serviceOrderTrend(@RequestParam(defaultValue = "6") int months) {
        return Result.success(operationStatisticsService.getServiceOrderTrend(
                SecurityContextHolder.getCompanyId(), months));
    }
}
