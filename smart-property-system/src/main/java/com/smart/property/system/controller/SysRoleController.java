package com.smart.property.system.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.system.dto.SysRoleDTO;
import com.smart.property.system.service.SysRoleService;
import com.smart.property.system.vo.SysRoleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 角色管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/system/roles")
@Tag(name = "角色")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping
    @OperLog(module = "角色管理", businessType = 4, description = "查询角色")
    public Result<PageResult<SysRoleVO>> list(PageQuery query) {
        return Result.success(sysRoleService.getRolePage(query, SecurityContextHolder.getCompanyId()));
    }

    @GetMapping("/{id}")
    @OperLog(module = "角色管理", businessType = 4, description = "查询角色详情")
    public Result<SysRoleVO> getById(@PathVariable Long id) {
        return Result.success(sysRoleService.getRoleById(id));
    }

    @PostMapping
    @OperLog(module = "角色管理", businessType = 1, description = "新增角色")
    public Result<Void> add(@Valid @RequestBody SysRoleDTO dto) {
        sysRoleService.createRole(dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "角色管理", businessType = 2, description = "修改角色")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysRoleDTO dto) {
        sysRoleService.updateRole(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "角色管理", businessType = 3, description = "删除角色")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.removeById(id);
        return Result.success();
    }

    @PutMapping("/{id}/menus")
    @OperLog(module = "角色管理", businessType = 2, description = "分配菜单")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        sysRoleService.assignMenus(id, menuIds);
        return Result.success();
    }
}