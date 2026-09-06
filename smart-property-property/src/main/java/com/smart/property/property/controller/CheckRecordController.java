package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.CheckRecordDTO;
import com.smart.property.property.service.CheckRecordService;
import com.smart.property.property.vo.CheckRecordVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 验房记录控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/property/check-records")
@Tag(name = "验房")
@RequiredArgsConstructor
public class CheckRecordController {

    private final CheckRecordService checkRecordService;

    @GetMapping
    @OperLog(module = "验房管理", businessType = 4, description = "查询验房记录")
    public Result<PageResult<CheckRecordVO>> list(PageQuery query,
                                                  @RequestParam(required = false) Long roomId) {
        PageResult<CheckRecordVO> result = checkRecordService.getCheckRecordPage(query,
                SecurityContextHolder.getCompanyId(), roomId);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @OperLog(module = "验房管理", businessType = 4, description = "查询验房记录详情")
    public Result<CheckRecordVO> getById(@PathVariable Long id) {
        return Result.success(checkRecordService.getByCheckId(id, SecurityContextHolder.getCompanyId()));
    }

    @PostMapping
    @OperLog(module = "验房管理", businessType = 1, description = "新增验房记录")
    public Result<Void> add(@Valid @RequestBody CheckRecordDTO dto) {
        checkRecordService.createCheckRecord(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/complete")
    @OperLog(module = "验房管理", businessType = 2, description = "整改完成")
    public Result<Void> complete(@PathVariable Long id) {
        checkRecordService.completeRectification(id,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "验房管理", businessType = 3, description = "删除验房记录")
    public Result<Void> delete(@PathVariable Long id) {
        checkRecordService.deleteCheckRecord(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }
}