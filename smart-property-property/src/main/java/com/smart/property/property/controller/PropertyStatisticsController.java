package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.Result;
import com.smart.property.property.service.PropertyStatisticsService;
import com.smart.property.property.vo.PropertyOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 房产财务统计控制器
 *
 * @author zzz
 * @since 2026-07-29
 */
@RestController
@RequestMapping("/api/v1/property/statistics")
@Tag(name = "房产统计")
@RequiredArgsConstructor
public class PropertyStatisticsController {

    private final PropertyStatisticsService propertyStatisticsService;

    @GetMapping("/overview")
    public Result<PropertyOverviewVO> overview() {
        return Result.success(propertyStatisticsService.getOverview(SecurityContextHolder.getCompanyId()));
    }

    @GetMapping("/payment-trend")
    public Result<List<Map<String, Object>>> paymentTrend(@RequestParam(defaultValue = "6") int months) {
        return Result.success(propertyStatisticsService.getPaymentTrend(
                SecurityContextHolder.getCompanyId(), months));
    }

    @GetMapping("/fee-status")
    public Result<List<Map<String, Object>>> feeStatus() {
        return Result.success(propertyStatisticsService.getFeeStatusDistribution(
                SecurityContextHolder.getCompanyId()));
    }
}
