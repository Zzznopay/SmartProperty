package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.RentPaymentDTO;
import com.smart.property.property.service.RentPaymentService;
import com.smart.property.property.vo.RentPaymentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 租金收取控制器
 *
 * @author zzz
 * @since 2026-07-28
 */
@RestController
@RequestMapping("/api/v1/property/rent-payments")
@Tag(name = "租金缴费")
@RequiredArgsConstructor
public class RentPaymentController {

    private final RentPaymentService rentPaymentService;

    @GetMapping
    @OperLog(module = "租金管理", businessType = 4, description = "查询租金记录")
    public Result<PageResult<RentPaymentVO>> list(PageQuery query,
                                                   @RequestParam(required = false) Long contractId,
                                                   @RequestParam(required = false) Integer status) {
        return Result.success(rentPaymentService.getRentPaymentPage(query,
                SecurityContextHolder.getCompanyId(), contractId, status));
    }

    @GetMapping("/{id}")
    @OperLog(module = "租金管理", businessType = 4, description = "查询租金详情")
    public Result<RentPaymentVO> getById(@PathVariable Long id) {
        return Result.success(rentPaymentService.getRentPaymentById(id, SecurityContextHolder.getCompanyId()));
    }

    @PostMapping
    @OperLog(module = "租金管理", businessType = 1, description = "收取租金")
    public Result<Void> collect(@Valid @RequestBody RentPaymentDTO dto) {
        rentPaymentService.collectRent(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/refund")
    @OperLog(module = "租金管理", businessType = 2, description = "租金退款")
    public Result<Void> refund(@PathVariable Long id, @RequestParam String remark) {
        rentPaymentService.refundRent(id, SecurityContextHolder.getCompanyId(), remark,
                SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/void")
    @OperLog(module = "租金管理", businessType = 2, description = "租金作废")
    public Result<Void> voidPayment(@PathVariable Long id, @RequestParam String remark) {
        rentPaymentService.voidRent(id, SecurityContextHolder.getCompanyId(), remark,
                SecurityContextHolder.getUsername());
        return Result.success();
    }
}