package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.DecorationRecordDTO;
import com.smart.property.property.service.DecorationRecordService;
import com.smart.property.property.vo.DecorationRecordVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 装修记录控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/property/decoration-records")
@Tag(name = "装修")
@RequiredArgsConstructor
public class DecorationRecordController {

    private final DecorationRecordService decorationRecordService;

    @GetMapping
    @OperLog(module = "装修管理", businessType = 4, description = "查询装修记录")
    public Result<PageResult<DecorationRecordVO>> list(PageQuery query,
                                                       @RequestParam(required = false) Long roomId) {
        PageResult<DecorationRecordVO> result = decorationRecordService.getDecorationPage(query,
                SecurityContextHolder.getCompanyId(), roomId);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @OperLog(module = "装修管理", businessType = 4, description = "查询装修记录详情")
    public Result<DecorationRecordVO> getById(@PathVariable Long id) {
        return Result.success(decorationRecordService.getByDecorationId(id, SecurityContextHolder.getCompanyId()));
    }

    @PostMapping
    @OperLog(module = "装修管理", businessType = 1, description = "装修申请")
    public Result<Void> add(@Valid @RequestBody DecorationRecordDTO dto) {
        decorationRecordService.createDecoration(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/start")
    @OperLog(module = "装修管理", businessType = 2, description = "开工")
    public Result<Void> start(@PathVariable Long id) {
        decorationRecordService.startDecoration(id,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/complete")
    @OperLog(module = "装修管理", businessType = 2, description = "完工")
    public Result<Void> complete(@PathVariable Long id) {
        decorationRecordService.completeDecoration(id,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/check")
    @OperLog(module = "装修管理", businessType = 2, description = "验收")
    public Result<Void> check(@PathVariable Long id, @RequestParam Integer checkResult) {
        decorationRecordService.checkDecoration(id,
                SecurityContextHolder.getCompanyId(), checkResult, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "装修管理", businessType = 3, description = "删除装修记录")
    public Result<Void> delete(@PathVariable Long id) {
        decorationRecordService.deleteDecoration(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }
}