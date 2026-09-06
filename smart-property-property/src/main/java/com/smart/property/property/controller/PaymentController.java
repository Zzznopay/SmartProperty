package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.PaymentDTO;
import com.smart.property.property.service.PaymentService;
import com.smart.property.property.vo.PaymentDetailVO;
import com.smart.property.property.vo.PaymentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 收费管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/finance/payments")
@Tag(name = "收费")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    @OperLog(module = "收费管理", businessType = 4, description = "查询收费记录")
    public Result<PageResult<PaymentVO>> list(PageQuery query,
                                            @RequestParam(required = false) Long roomId,
                                            @RequestParam(required = false) Long ownerId) {
        PageResult<PaymentVO> result = paymentService.getPaymentPage(query,
                SecurityContextHolder.getCompanyId(), roomId, ownerId);
        return Result.success(result);
    }

    @PostMapping
    @OperLog(module = "收费管理", businessType = 1, description = "收取物业费")
    public Result<Void> collect(@Valid @RequestBody PaymentDTO dto) {
        paymentService.collectPayment(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/with-details")
    @OperLog(module = "收费管理", businessType = 1, description = "收取物业费（批量明细）")
    public Result<Void> collectWithDetails(@Valid @RequestBody PaymentDTO dto) {
        paymentService.collectPayment(dto, dto.getDetails(),
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @GetMapping("/{id}")
    @OperLog(module = "收费管理", businessType = 4, description = "查询收费详情")
    public Result<PaymentVO> getById(@PathVariable Long id) {
        return Result.success(paymentService.getPaymentById(id));
    }

    @GetMapping("/{id}/details")
    @OperLog(module = "收费管理", businessType = 4, description = "查询收费明细")
    public Result<List<PaymentDetailVO>> details(@PathVariable Long id) {
        return Result.success(paymentService.getPaymentDetails(id));
    }

    @PostMapping("/{id}/audit")
    @OperLog(module = "收费管理", businessType = 2, description = "收费审核")
    public Result<Void> audit(@PathVariable Long id) {
        paymentService.auditPayment(id, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/refund")
    @OperLog(module = "收费管理", businessType = 2, description = "物业费退款")
    public Result<Void> refund(@PathVariable Long id, @RequestParam String remark) {
        paymentService.refundPayment(id, remark, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/void")
    @OperLog(module = "收费管理", businessType = 2, description = "物业费作废")
    public Result<Void> voidPayment(@PathVariable Long id, @RequestParam String remark) {
        paymentService.voidPayment(id, remark, SecurityContextHolder.getUsername());
        return Result.success();
    }
}
