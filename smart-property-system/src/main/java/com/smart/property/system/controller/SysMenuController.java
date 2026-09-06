package com.smart.property.system.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.system.dto.SysMenuDTO;
import com.smart.property.system.service.SysMenuService;
import com.smart.property.system.vo.MenuTreeNode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 菜单管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/system/menus")
@Tag(name = "菜单")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @GetMapping("/tree")
    @OperLog(module = "菜单管理", businessType = 4, description = "查询菜单树")
    public Result<List<MenuTreeNode>> tree() {
        return Result.success(sysMenuService.getMenuTree());
    }

    @GetMapping("/{id}")
    @OperLog(module = "菜单管理", businessType = 4, description = "查询菜单详情")
    public Result<MenuTreeNode> getById(@PathVariable Long id) {
        return Result.success(sysMenuService.getMenuById(id));
    }

    @PostMapping
    @OperLog(module = "菜单管理", businessType = 1, description = "新增菜单")
    public Result<Void> add(@Valid @RequestBody SysMenuDTO dto) {
        sysMenuService.createMenu(dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "菜单管理", businessType = 2, description = "修改菜单")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysMenuDTO dto) {
        sysMenuService.updateMenu(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "菜单管理", businessType = 3, description = "删除菜单")
    public Result<Void> delete(@PathVariable Long id) {
        sysMenuService.removeById(id);
        return Result.success();
    }
}