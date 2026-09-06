package com.smart.property.system.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.system.dto.SysDictTypeDTO;
import com.smart.property.system.service.SysDictTypeService;
import com.smart.property.system.vo.SysDictTypeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/system/dict-types")
@Tag(name = "字典类型")
@RequiredArgsConstructor
public class SysDictTypeController {

    private final SysDictTypeService sysDictTypeService;

    @GetMapping
    @OperLog(module = "字典管理", businessType = 4, description = "查询字典类型")
    public Result<PageResult<SysDictTypeVO>> list(PageQuery query) {
        return Result.success(sysDictTypeService.getDictTypePage(query, SecurityContextHolder.getCompanyId()));
    }

    @PostMapping
    @OperLog(module = "字典管理", businessType = 1, description = "新增字典类型")
    public Result<Void> add(@Valid @RequestBody SysDictTypeDTO dto) {
        sysDictTypeService.createDictType(dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "字典管理", businessType = 2, description = "修改字典类型")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysDictTypeDTO dto) {
        sysDictTypeService.updateDictType(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "字典管理", businessType = 3, description = "删除字典类型")
    public Result<Void> delete(@PathVariable Long id) {
        sysDictTypeService.removeById(id);
        return Result.success();
    }
}