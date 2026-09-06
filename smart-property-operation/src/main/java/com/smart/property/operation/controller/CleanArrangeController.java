package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.CleanArrangeDTO;
import com.smart.property.operation.service.CleanArrangeService;
import com.smart.property.operation.vo.CleanArrangeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 清洁安排控制器
 */
@RestController
@RequestMapping("/api/v1/operation/clean-arranges")
@Tag(name = "清洁安排")
@RequiredArgsConstructor
public class CleanArrangeController {

    private final CleanArrangeService cleanArrangeService;

    @GetMapping
    @OperLog(module = "清洁管理", businessType = 4, description = "查询清洁安排")
    public Result<PageResult<CleanArrangeVO>> list(PageQuery query,
                                                  @RequestParam(required = false) Long communityId,
                                                  @RequestParam(required = false) LocalDate arrangeDate) {
        PageResult<CleanArrangeVO> result = cleanArrangeService.getCleanArrangePage(query,
                SecurityContextHolder.getCompanyId(), communityId, arrangeDate);
        return Result.success(result);
    }

    @PostMapping
    @OperLog(module = "清洁管理", businessType = 1, description = "新增清洁安排")
    public Result<Void> add(@Valid @RequestBody CleanArrangeDTO dto) {
        cleanArrangeService.createCleanArrange(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/complete")
    @OperLog(module = "清洁管理", businessType = 2, description = "完成清洁")
    public Result<Void> complete(@PathVariable Long id) {
        cleanArrangeService.completeClean(id, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "清洁管理", businessType = 3, description = "删除清洁安排")
    public Result<Void> delete(@PathVariable Long id) {
        cleanArrangeService.removeById(id);
        return Result.success();
    }
}
