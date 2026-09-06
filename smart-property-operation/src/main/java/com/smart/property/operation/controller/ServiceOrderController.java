package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.ServiceOrderDTO;
import com.smart.property.operation.service.ServiceOrderService;
import com.smart.property.operation.vo.ServiceOrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 服务工单控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/operation/service-orders")
@Tag(name = "服务工单")
@RequiredArgsConstructor
public class ServiceOrderController {

    private final ServiceOrderService serviceOrderService;

    @GetMapping
    @OperLog(module = "服务工单", businessType = 4, description = "查询工单列表")
    public Result<PageResult<ServiceOrderVO>> list(PageQuery query,
                                                  @RequestParam(required = false) Integer orderType,
                                                  @RequestParam(required = false) Integer status) {
        PageResult<ServiceOrderVO> result = serviceOrderService.getOrderPage(query,
                SecurityContextHolder.getCompanyId(), orderType, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @OperLog(module = "服务工单", businessType = 4, description = "查询工单详情")
    public Result<ServiceOrderVO> getById(@PathVariable Long id) {
        return Result.success(serviceOrderService.getOrderById(id));
    }

    @PostMapping
    @OperLog(module = "服务工单", businessType = 1, description = "创建工单")
    public Result<Void> create(@Valid @RequestBody ServiceOrderDTO dto) {
        serviceOrderService.createOrder(dto,
                SecurityContextHolder.getCompanyId(),
                SecurityContextHolder.getUserId(),
                SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/assign")
    @OperLog(module = "服务工单", businessType = 2, description = "分配工单")
    public Result<Void> assign(@PathVariable Long id,
                               @RequestParam Long assignUserId,
                               @RequestParam String assignUserName) {
        serviceOrderService.assignOrder(id, assignUserId, assignUserName,
                SecurityContextHolder.getUserId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/handle")
    @OperLog(module = "服务工单", businessType = 2, description = "处理工单")
    public Result<Void> handle(@PathVariable Long id, @RequestParam String handleContent) {
        serviceOrderService.handleOrder(id, handleContent,
                SecurityContextHolder.getUserId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/visit")
    @OperLog(module = "服务工单", businessType = 2, description = "回访工单")
    public Result<Void> visit(@PathVariable Long id,
                              @RequestParam String visitContent,
                              @RequestParam Integer visitScore) {
        serviceOrderService.visitOrder(id, visitContent, visitScore,
                SecurityContextHolder.getUserId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/close")
    @OperLog(module = "服务工单", businessType = 2, description = "关闭工单")
    public Result<Void> close(@PathVariable Long id) {
        serviceOrderService.closeOrder(id,
                SecurityContextHolder.getUserId(), SecurityContextHolder.getUsername());
        return Result.success();
    }
}