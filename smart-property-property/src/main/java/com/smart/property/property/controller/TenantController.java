package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.TenantDTO;
import com.smart.property.property.service.TenantService;
import com.smart.property.property.vo.TenantVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 租户管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/property/tenants")
@Tag(name = "租户")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    @OperLog(module = "租户管理", businessType = 4, description = "查询租户列表")
    public Result<PageResult<TenantVO>> list(PageQuery query) {
        PageResult<TenantVO> result = tenantService.getTenantPage(query, SecurityContextHolder.getCompanyId());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @OperLog(module = "租户管理", businessType = 4, description = "查询租户详情")
    public Result<TenantVO> getById(@PathVariable Long id) {
        TenantVO tenant = tenantService.getTenantById(id, SecurityContextHolder.getCompanyId());
        return Result.success(tenant);
    }

    @PostMapping
    @OperLog(module = "租户管理", businessType = 1, description = "新增租户")
    public Result<Void> add(@Valid @RequestBody TenantDTO dto) {
        tenantService.createTenant(dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "租户管理", businessType = 2, description = "修改租户")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TenantDTO dto) {
        tenantService.updateTenant(id, dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "租户管理", businessType = 3, description = "删除租户")
    public Result<Void> delete(@PathVariable Long id) {
        tenantService.deleteTenant(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }
}