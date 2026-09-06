package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.GreeneryCheckDTO;
import com.smart.property.operation.service.GreeneryCheckService;
import com.smart.property.operation.vo.GreeneryCheckVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/operation/greenery-checks")
@Tag(name = "绿化检查")
@RequiredArgsConstructor
public class GreeneryCheckController {

    private final GreeneryCheckService greeneryCheckService;

    @GetMapping
    @OperLog(module = "绿化检查", businessType = 4, description = "查询绿化检查")
    public Result<PageResult<GreeneryCheckVO>> list(PageQuery query,
                                                  @RequestParam(required = false) Long communityId) {
        return Result.success(greeneryCheckService.getPage(query,
                SecurityContextHolder.getCompanyId(), communityId));
    }

    @GetMapping("/{id}")
    public Result<GreeneryCheckVO> getById(@PathVariable Long id) {
        return Result.success(greeneryCheckService.getById(id));
    }

    @PostMapping
    @OperLog(module = "绿化检查", businessType = 1, description = "新增绿化检查")
    public Result<Void> add(@Valid @RequestBody GreeneryCheckDTO dto) {
        greeneryCheckService.create(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "绿化检查", businessType = 2, description = "修改绿化检查")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody GreeneryCheckDTO dto) {
        greeneryCheckService.update(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "绿化检查", businessType = 3, description = "删除绿化检查")
    public Result<Void> delete(@PathVariable Long id) {
        greeneryCheckService.removeById(id);
        return Result.success();
    }
}