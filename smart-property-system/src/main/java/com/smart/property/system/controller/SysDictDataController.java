package com.smart.property.system.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.system.dto.SysDictDataDTO;
import com.smart.property.system.service.SysDictDataService;
import com.smart.property.system.vo.SysDictDataVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/system/dict-data")
@Tag(name = "字典数据")
@RequiredArgsConstructor
public class SysDictDataController {

    private final SysDictDataService sysDictDataService;

    @GetMapping
    @OperLog(module = "字典管理", businessType = 4, description = "查询字典数据列表")
    public Result<PageResult<SysDictDataVO>> page(PageQuery query,
                                                  @RequestParam(required = false) String dictType) {
        return Result.success(sysDictDataService.getDictDataPage(query, dictType, SecurityContextHolder.getCompanyId()));
    }

    @GetMapping("/list")
    @OperLog(module = "字典管理", businessType = 4, description = "按类型查询字典数据")
    public Result<List<SysDictDataVO>> listByType(@RequestParam String dictType) {
        return Result.success(sysDictDataService.listByDictType(dictType));
    }

    @PostMapping
    @OperLog(module = "字典管理", businessType = 1, description = "新增字典数据")
    public Result<Void> add(@Valid @RequestBody SysDictDataDTO dto) {
        sysDictDataService.createDictData(dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "字典管理", businessType = 2, description = "修改字典数据")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody SysDictDataDTO dto) {
        sysDictDataService.updateDictData(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "字典管理", businessType = 3, description = "删除字典数据")
    public Result<Void> delete(@PathVariable Long id) {
        sysDictDataService.removeById(id);
        return Result.success();
    }
}