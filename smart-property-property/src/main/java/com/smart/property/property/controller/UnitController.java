package com.smart.property.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.domain.Unit;
import com.smart.property.property.dto.UnitDTO;
import com.smart.property.property.service.UnitService;
import com.smart.property.property.vo.UnitVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 单元管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/property/units")
@Tag(name = "单元")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @GetMapping
    @OperLog(module = "单元管理", businessType = 4, description = "查询单元列表")
    public Result<Page<UnitVO>> list(@RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize,
                                     @RequestParam(required = false) Long buildingId,
                                     @RequestParam(required = false) String unitCode,
                                     @RequestParam(required = false) String unitName,
                                     @RequestParam(required = false) Integer status) {
        Page<Unit> page = new Page<>(pageNum, pageSize);
        return Result.success(unitService.getUnitPage(page, SecurityContextHolder.getCompanyId(),
                buildingId, unitCode, unitName, status));
    }

    @GetMapping("/by-building/{buildingId}")
    @OperLog(module = "单元管理", businessType = 4, description = "按楼宇查询单元")
    public Result<List<UnitVO>> listByBuilding(@PathVariable Long buildingId) {
        return Result.success(unitService.getUnitsByBuildingId(buildingId, SecurityContextHolder.getCompanyId()));
    }

    @GetMapping("/{id}")
    @OperLog(module = "单元管理", businessType = 4, description = "查询单元详情")
    public Result<UnitVO> getById(@PathVariable Long id) {
        return Result.success(unitService.getUnitById(id, SecurityContextHolder.getCompanyId()));
    }

    @PostMapping
    @OperLog(module = "单元管理", businessType = 1, description = "新增单元")
    public Result<Void> add(@Valid @RequestBody UnitDTO dto) {
        unitService.createUnit(dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "单元管理", businessType = 2, description = "修改单元")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UnitDTO dto) {
        unitService.updateUnit(id, dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "单元管理", businessType = 3, description = "删除单元")
    public Result<Void> delete(@PathVariable Long id) {
        unitService.deleteUnit(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }
}