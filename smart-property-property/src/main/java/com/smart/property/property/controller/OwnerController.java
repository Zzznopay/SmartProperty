package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.OwnerDTO;
import com.smart.property.property.service.OwnerService;
import com.smart.property.property.vo.OwnerVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 业主管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/property/owners")
@Tag(name = "业主")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @GetMapping
    @OperLog(module = "业主管理", businessType = 4, description = "查询业主列表")
    public Result<PageResult<OwnerVO>> list(PageQuery query) {
        PageResult<OwnerVO> result = ownerService.getOwnerPage(query, SecurityContextHolder.getCompanyId());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @OperLog(module = "业主管理", businessType = 4, description = "查询业主详情")
    public Result<OwnerVO> getById(@PathVariable Long id) {
        OwnerVO owner = ownerService.getOwnerById(id, SecurityContextHolder.getCompanyId());
        return Result.success(owner);
    }

    @PostMapping
    @OperLog(module = "业主管理", businessType = 1, description = "新增业主")
    public Result<Void> add(@Valid @RequestBody OwnerDTO dto) {
        ownerService.addOwner(dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "业主管理", businessType = 2, description = "修改业主")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody OwnerDTO dto) {
        ownerService.updateOwner(id, dto,
                String.valueOf(SecurityContextHolder.getCompanyId()),
                SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "业主管理", businessType = 3, description = "删除业主")
    public Result<Void> delete(@PathVariable Long id) {
        ownerService.deleteOwner(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }
}