package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.BlueprintDTO;
import com.smart.property.property.service.BlueprintService;
import com.smart.property.property.vo.BlueprintVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 图纸控制器
 *
 * @author zzz
 * @since 2026-07-28
 */
@RestController
@RequestMapping("/api/v1/property/blueprints")
@Tag(name = "蓝图")
@RequiredArgsConstructor
public class BlueprintController {

    private final BlueprintService blueprintService;

    @GetMapping
    @OperLog(module = "图纸管理", businessType = 4, description = "查询图纸")
    public Result<PageResult<BlueprintVO>> list(PageQuery query,
                                                @RequestParam(required = false) Long communityId,
                                                @RequestParam(required = false) Long buildingId,
                                                @RequestParam(required = false) Integer blueprintType) {
        return Result.success(blueprintService.getBlueprintPage(query,
                SecurityContextHolder.getCompanyId(), communityId, buildingId, blueprintType));
    }

    @PostMapping
    @OperLog(module = "图纸管理", businessType = 1, description = "上传图纸")
    public Result<Void> add(@Valid @RequestBody BlueprintDTO dto) {
        blueprintService.createBlueprint(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "图纸管理", businessType = 3, description = "删除图纸")
    public Result<Void> delete(@PathVariable Long id) {
        blueprintService.removeById(id);
        return Result.success();
    }
}