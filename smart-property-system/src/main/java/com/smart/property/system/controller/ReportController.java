package com.smart.property.system.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.system.service.ReportService;
import com.smart.property.system.vo.PaymentStatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/report")
@Tag(name = "报表")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/statistics/payment")
    @OperLog(module = "报表统计", businessType = 4, description = "缴费统计")
    public Result<List<PaymentStatisticsVO>> paymentStatistics(@RequestParam String startMonth,
                                                                 @RequestParam String endMonth) {
        return Result.success(reportService.paymentStatistics(SecurityContextHolder.getCompanyId(), startMonth, endMonth));
    }
}