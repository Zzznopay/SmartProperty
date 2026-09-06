package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.RegulationDTO;
import com.smart.property.operation.service.RegulationService;
import com.smart.property.operation.vo.RegulationVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 规章制度控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/admin/regulations")
@Tag(name = "规章制度")
@RequiredArgsConstructor
public class RegulationController {

    private final RegulationService regulationService;

    @GetMapping
    @OperLog(module = "规章制度", businessType = 4, description = "查询规章制度")
    public Result<PageResult<RegulationVO>> list(PageQuery query,
                                                @RequestParam(required = false) String category) {
        PageResult<RegulationVO> result = regulationService.getRegulationPage(query,
                SecurityContextHolder.getCompanyId(), category);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @OperLog(module = "规章制度", businessType = 4, description = "查询制度详情")
    public Result<RegulationVO> getById(@PathVariable Long id) {
        RegulationVO vo = regulationService.getRegulationVOById(id);
        regulationService.incrementViewCount(id);
        return Result.success(vo);
    }

    @PostMapping
    @OperLog(module = "规章制度", businessType = 1, description = "新增规章制度")
    public Result<Void> add(@Valid @RequestBody RegulationDTO dto) {
        regulationService.createRegulation(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/publish")
    @OperLog(module = "规章制度", businessType = 2, description = "发布制度")
    public Result<Void> publish(@PathVariable Long id) {
        regulationService.publishRegulation(id, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "规章制度", businessType = 3, description = "删除规章制度")
    public Result<Void> delete(@PathVariable Long id) {
        regulationService.removeById(id);
        return Result.success();
    }
}