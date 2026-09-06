package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.DutyRecordDTO;
import com.smart.property.operation.service.DutyRecordService;
import com.smart.property.operation.vo.DutyRecordVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/operation/duty-records")
@Tag(name = "执勤记录")
@RequiredArgsConstructor
public class DutyRecordController {

    private final DutyRecordService dutyRecordService;

    @GetMapping
    @OperLog(module = "执勤管理", businessType = 4, description = "查询执勤记录")
    public Result<PageResult<DutyRecordVO>> list(PageQuery query,
                                                @RequestParam(required = false) Long communityId) {
        return Result.success(dutyRecordService.getPage(query,
                SecurityContextHolder.getCompanyId(), communityId));
    }

    @GetMapping("/{id}")
    public Result<DutyRecordVO> getById(@PathVariable Long id) {
        return Result.success(dutyRecordService.getById(id));
    }

    @PostMapping
    @OperLog(module = "执勤管理", businessType = 1, description = "新增执勤记录")
    public Result<Void> add(@Valid @RequestBody DutyRecordDTO dto) {
        dutyRecordService.create(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "执勤管理", businessType = 2, description = "修改执勤记录")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody DutyRecordDTO dto) {
        dutyRecordService.update(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "执勤管理", businessType = 3, description = "删除执勤记录")
    public Result<Void> delete(@PathVariable Long id) {
        dutyRecordService.removeById(id);
        return Result.success();
    }
}