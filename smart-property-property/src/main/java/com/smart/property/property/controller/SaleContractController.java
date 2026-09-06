package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.SaleContractDTO;
import com.smart.property.property.service.SaleContractService;
import com.smart.property.property.vo.SaleContractVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 销售合同控制器
 *
 * @author zzz
 * @since 2026-07-28
 */
@RestController
@RequestMapping("/api/v1/property/sale-contracts")
@Tag(name = "销售合同")
@RequiredArgsConstructor
public class SaleContractController {

    private final SaleContractService saleContractService;

    @GetMapping
    @OperLog(module = "销售合同", businessType = 4, description = "查询销售合同")
    public Result<PageResult<SaleContractVO>> list(PageQuery query,
                                                   @RequestParam(required = false) Long roomId,
                                                   @RequestParam(required = false) Integer status) {
        return Result.success(saleContractService.getSaleContractPage(query,
                SecurityContextHolder.getCompanyId(), roomId, status));
    }

    @GetMapping("/{id}")
    @OperLog(module = "销售合同", businessType = 4, description = "查询销售合同详情")
    public Result<SaleContractVO> getById(@PathVariable Long id) {
        return Result.success(saleContractService.getByContractId(id, SecurityContextHolder.getCompanyId()));
    }

    @PostMapping
    @OperLog(module = "销售合同", businessType = 1, description = "新增销售合同")
    public Result<Void> add(@Valid @RequestBody SaleContractDTO dto) {
        saleContractService.createSaleContract(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "销售合同", businessType = 2, description = "修改销售合同")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SaleContractDTO dto) {
        saleContractService.updateSaleContract(id, dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/deliver")
    @OperLog(module = "销售合同", businessType = 2, description = "房屋交付")
    public Result<Void> deliver(@PathVariable Long id) {
        saleContractService.deliver(id,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "销售合同", businessType = 3, description = "作废销售合同")
    public Result<Void> delete(@PathVariable Long id) {
        saleContractService.deleteSaleContract(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }
}