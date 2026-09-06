package com.smart.property.property.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.domain.Building;
import com.smart.property.property.dto.BuildingDTO;
import com.smart.property.property.service.BuildingService;
import com.smart.property.property.vo.BuildingVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 楼宇管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/property/buildings")
@Tag(name = "楼宇")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping
    @OperLog(module = "楼宇管理", businessType = 4, description = "查询楼宇列表")
    public Result<Page<BuildingVO>> list(@RequestParam(defaultValue = "1") int pageNum,
                                         @RequestParam(defaultValue = "10") int pageSize,
                                         @RequestParam(required = false) Long communityId,
                                         @RequestParam(required = false) String buildingCode,
                                         @RequestParam(required = false) String buildingName,
                                         @RequestParam(required = false) Integer status) {
        Page<Building> page = new Page<>(pageNum, pageSize);
        return Result.success(buildingService.getBuildingPage(page, SecurityContextHolder.getCompanyId(),
                communityId, buildingCode, buildingName, status));
    }

    @GetMapping("/by-community/{communityId}")
    @OperLog(module = "楼宇管理", businessType = 4, description = "按小区查询楼宇")
    public Result<List<BuildingVO>> listByCommunity(@PathVariable Long communityId) {
        return Result.success(buildingService.getBuildingsByCommunityId(communityId, SecurityContextHolder.getCompanyId()));
    }

    @GetMapping("/{id}")
    @OperLog(module = "楼宇管理", businessType = 4, description = "查询楼宇详情")
    public Result<BuildingVO> getById(@PathVariable Long id) {
        return Result.success(buildingService.getBuildingById(id, SecurityContextHolder.getCompanyId()));
    }

    @PostMapping
    @OperLog(module = "楼宇管理", businessType = 1, description = "新增楼宇")
    public Result<Void> add(@Valid @RequestBody BuildingDTO dto) {
        buildingService.createBuilding(dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "楼宇管理", businessType = 2, description = "修改楼宇")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody BuildingDTO dto) {
        buildingService.updateBuilding(id, dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "楼宇管理", businessType = 3, description = "删除楼宇")
    public Result<Void> delete(@PathVariable Long id) {
        buildingService.deleteBuilding(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }
}