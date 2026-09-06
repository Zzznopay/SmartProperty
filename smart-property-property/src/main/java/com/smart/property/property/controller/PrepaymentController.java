package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.PrepaymentDTO;
import com.smart.property.property.service.PrepaymentService;
import com.smart.property.property.vo.PrepaymentUsageVO;
import com.smart.property.property.vo.PrepaymentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 预收款管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/finance/prepayments")
@Tag(name = "预收款")
@RequiredArgsConstructor
public class PrepaymentController {

    private final PrepaymentService prepaymentService;

    @GetMapping
    @OperLog(module = "预收款管理", businessType = 4, description = "查询预收款列表")
    public Result<PageResult<PrepaymentVO>> list(PageQuery query,
                                                 @RequestParam(required = false) Long ownerId) {
        PageResult<PrepaymentVO> result = prepaymentService.getPrepaymentPage(query,
                SecurityContextHolder.getCompanyId(), ownerId);
        return Result.success(result);
    }

    @PostMapping
    @OperLog(module = "预收款管理", businessType = 1, description = "新增预收款")
    public Result<Void> add(@Valid @RequestBody PrepaymentDTO dto) {
        prepaymentService.createPrepayment(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @GetMapping("/balance/{ownerId}")
    public Result<BigDecimal> getBalance(@PathVariable Long ownerId) {
        return Result.success(prepaymentService.getBalance(ownerId, SecurityContextHolder.getCompanyId()));
    }

    @PostMapping("/{id}/refund")
    @OperLog(module = "预收款管理", businessType = 2, description = "退还预收款")
    public Result<Void> refund(@PathVariable Long id) {
        prepaymentService.refundPrepayment(id,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @GetMapping("/{id}")
    @OperLog(module = "预收款管理", businessType = 4, description = "查询预收款详情")
    public Result<PrepaymentVO> getById(@PathVariable Long id) {
        return Result.success(prepaymentService.getById(id, SecurityContextHolder.getCompanyId()));
    }

    @PutMapping("/{id}")
    @OperLog(module = "预收款管理", businessType = 2, description = "修改预收款")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody PrepaymentDTO dto) {
        prepaymentService.updatePrepayment(id, dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @GetMapping("/{id}/usages")
    @OperLog(module = "预收款管理", businessType = 4, description = "查询预收款使用记录")
    public Result<List<PrepaymentUsageVO>> usages(@PathVariable Long id) {
        return Result.success(prepaymentService.getUsages(id, SecurityContextHolder.getCompanyId()));
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "预收款管理", businessType = 3, description = "删除预收款")
    public Result<Void> delete(@PathVariable Long id) {
        prepaymentService.deletePrepayment(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }
}