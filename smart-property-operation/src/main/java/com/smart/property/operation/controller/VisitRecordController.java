package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.VisitRecordDTO;
import com.smart.property.operation.service.VisitRecordService;
import com.smart.property.operation.vo.VisitRecordVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 来访登记控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/operation/visit-records")
@Tag(name = "来访登记")
@RequiredArgsConstructor
public class VisitRecordController {

    private final VisitRecordService visitRecordService;

    @GetMapping
    @OperLog(module = "来访管理", businessType = 4, description = "查询来访记录")
    public Result<PageResult<VisitRecordVO>> list(PageQuery query,
                                                  @RequestParam(required = false) Long communityId) {
        PageResult<VisitRecordVO> result = visitRecordService.getVisitPage(query,
                SecurityContextHolder.getCompanyId(), communityId);
        return Result.success(result);
    }

    @PostMapping
    @OperLog(module = "来访管理", businessType = 1, description = "来访登记")
    public Result<Void> register(@Valid @RequestBody VisitRecordDTO dto) {
        visitRecordService.registerVisit(dto,
                SecurityContextHolder.getCompanyId(),
                SecurityContextHolder.getUserId(),
                SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/leave")
    @OperLog(module = "来访管理", businessType = 2, description = "离开登记")
    public Result<Void> leave(@PathVariable Long id) {
        visitRecordService.registerLeave(id, SecurityContextHolder.getUsername());
        return Result.success();
    }
}