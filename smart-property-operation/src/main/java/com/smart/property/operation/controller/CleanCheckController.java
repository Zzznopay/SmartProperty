package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.CleanCheckDTO;
import com.smart.property.operation.service.CleanCheckService;
import com.smart.property.operation.vo.CleanCheckVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 清洁检查 Controller
 */
@RestController
@RequestMapping("/api/v1/operation/clean-checks")
@Tag(name = "清洁检查")
@RequiredArgsConstructor
public class CleanCheckController {

    private final CleanCheckService cleanCheckService;

    @GetMapping
    @OperLog(module = "清洁检查", businessType = 4, description = "查询清洁检查")
    public Result<PageResult<CleanCheckVO>> list(PageQuery query,
                                                 @RequestParam(required = false) Long communityId) {
        return Result.success(cleanCheckService.getPage(query,
                SecurityContextHolder.getCompanyId(), communityId));
    }

    @GetMapping("/{id}")
    @OperLog(module = "清洁检查", businessType = 4, description = "查询详情")
    public Result<CleanCheckVO> getById(@PathVariable Long id) {
        return Result.success(cleanCheckService.getById(id));
    }

    @PostMapping
    @OperLog(module = "清洁检查", businessType = 1, description = "新增清洁检查")
    public Result<Void> add(@Valid @RequestBody CleanCheckDTO dto) {
        cleanCheckService.create(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "清洁检查", businessType = 2, description = "修改清洁检查")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CleanCheckDTO dto) {
        cleanCheckService.update(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "清洁检查", businessType = 3, description = "删除清洁检查")
    public Result<Void> delete(@PathVariable Long id) {
        cleanCheckService.removeById(id);
        return Result.success();
    }
}