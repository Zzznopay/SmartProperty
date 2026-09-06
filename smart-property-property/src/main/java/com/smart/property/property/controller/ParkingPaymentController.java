package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.ParkingPaymentDTO;
import com.smart.property.property.service.ParkingPaymentService;
import com.smart.property.property.vo.ParkingPaymentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 车位缴费控制器
 *
 * @author zzz
 * @since 2026-07-28
 */
@RestController
@RequestMapping("/api/v1/finance/parking-payments")
@Tag(name = "Parking Payment")
@RequiredArgsConstructor
public class ParkingPaymentController {

    private final ParkingPaymentService parkingPaymentService;

    @GetMapping
    @OperLog(module = "车位缴费", businessType = 4, description = "查询车位缴费记录")
    public Result<PageResult<ParkingPaymentVO>> list(PageQuery query,
                                                    @RequestParam(required = false) Long parkingId,
                                                    @RequestParam(required = false) Integer status) {
        return Result.success(parkingPaymentService.getPage(query,
                SecurityContextHolder.getCompanyId(), parkingId, status));
    }

    @GetMapping("/{id}")
    @OperLog(module = "车位缴费", businessType = 4, description = "查询缴费详情")
    public Result<ParkingPaymentVO> getById(@PathVariable Long id) {
        return Result.success(parkingPaymentService.getParkingPaymentById(id, SecurityContextHolder.getCompanyId()));
    }

    @PostMapping
    @OperLog(module = "车位缴费", businessType = 1, description = "车位缴费")
    public Result<Void> collect(@Valid @RequestBody ParkingPaymentDTO dto) {
        parkingPaymentService.collect(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "车位缴费", businessType = 2, description = "修改缴费记录")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ParkingPaymentDTO dto) {
        parkingPaymentService.updateParkingPayment(id, dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "车位缴费", businessType = 3, description = "删除缴费记录")
    public Result<Void> delete(@PathVariable Long id) {
        parkingPaymentService.deleteParkingPayment(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }

    @PostMapping("/{id}/refund")
    @OperLog(module = "车位缴费", businessType = 2, description = "缴费退款")
    public Result<Void> refund(@PathVariable Long id, @RequestParam String remark) {
        parkingPaymentService.refund(id, remark,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/void")
    @OperLog(module = "车位缴费", businessType = 2, description = "缴费作废")
    public Result<Void> voidPayment(@PathVariable Long id, @RequestParam String remark) {
        parkingPaymentService.voidPayment(id, remark,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }
}
