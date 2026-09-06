package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.InvoiceDTO;
import com.smart.property.property.service.InvoiceService;
import com.smart.property.property.vo.InvoiceVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 票据管理控制器
 *
 * @author zzz
 * @since 2026-07-28
 */
@RestController
@RequestMapping("/api/v1/finance/invoices")
@Tag(name = "发票")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    @OperLog(module = "票据管理", businessType = 4, description = "查询票据")
    public Result<PageResult<InvoiceVO>> list(PageQuery query,
                                               @RequestParam(required = false) Integer status,
                                               @RequestParam(required = false) Integer invoiceType) {
        return Result.success(invoiceService.getInvoicePage(query,
                SecurityContextHolder.getCompanyId(), status, invoiceType));
    }

    @PostMapping("/import")
    @OperLog(module = "票据管理", businessType = 1, description = "批量导入票据")
    public Result<Void> importInvoices(@Valid @RequestBody List<InvoiceDTO> invoices) {
        invoiceService.importInvoices(invoices,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/assign")
    @OperLog(module = "票据管理", businessType = 2, description = "票据分配")
    public Result<Integer> assign(@RequestParam("invoiceIds") List<Long> invoiceIds,
                                  @RequestParam Long userId,
                                  @RequestParam String userName) {
        return Result.success(invoiceService.assignInvoices(invoiceIds, userId, userName,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername()));
    }

    @PostMapping("/{id}/void-apply")
    @OperLog(module = "票据管理", businessType = 2, description = "作废申请")
    public Result<Void> applyVoid(@PathVariable Long id, @RequestParam String reason) {
        invoiceService.applyVoid(id, reason,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/void-confirm")
    @OperLog(module = "票据管理", businessType = 2, description = "作废确认")
    public Result<Void> confirmVoid(@PathVariable Long id) {
        invoiceService.confirmVoid(id,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "票据管理", businessType = 3, description = "删除票据")
    public Result<Void> delete(@PathVariable Long id) {
        invoiceService.deleteInvoice(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }
}