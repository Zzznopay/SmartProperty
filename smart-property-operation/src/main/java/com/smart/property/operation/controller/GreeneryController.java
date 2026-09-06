package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.GreeneryDTO;
import com.smart.property.operation.service.GreeneryService;
import com.smart.property.operation.vo.GreeneryVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 绿化管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/operation/greeneries")
@Tag(name = "绿化")
@RequiredArgsConstructor
public class GreeneryController {

    private final GreeneryService greeneryService;

    @GetMapping
    @OperLog(module = "绿化管理", businessType = 4, description = "查询绿化植被")
    public Result<PageResult<GreeneryVO>> list(PageQuery query,
                                              @RequestParam(required = false) Long communityId,
                                              @RequestParam(required = false) Integer greeneryType) {
        PageResult<GreeneryVO> result = greeneryService.getGreeneryPage(query,
                SecurityContextHolder.getCompanyId(), communityId, greeneryType);
        return Result.success(result);
    }

    @PostMapping
    @OperLog(module = "绿化管理", businessType = 1, description = "新增绿化植被")
    public Result<Void> add(@Valid @RequestBody GreeneryDTO dto) {
        greeneryService.createGreenery(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "绿化管理", businessType = 2, description = "修改绿化植被")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody GreeneryDTO dto) {
        greeneryService.updateGreenery(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "绿化管理", businessType = 3, description = "删除绿化植被")
    public Result<Void> delete(@PathVariable Long id) {
        greeneryService.removeById(id);
        return Result.success();
    }
}