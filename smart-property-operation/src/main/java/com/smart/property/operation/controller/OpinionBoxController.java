package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.OpinionBoxDTO;
import com.smart.property.operation.dto.OpinionSubmitDTO;
import com.smart.property.operation.service.OpinionBoxService;
import com.smart.property.operation.vo.OpinionBoxVO;
import com.smart.property.operation.vo.OpinionSubmitVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 意见箱控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/admin/opinion-boxes")
@Tag(name = "意见箱")
@RequiredArgsConstructor
public class OpinionBoxController {

    private final OpinionBoxService opinionBoxService;

    @GetMapping
    @OperLog(module = "意见箱", businessType = 4, description = "查询意见箱")
    public Result<PageResult<OpinionBoxVO>> list(PageQuery query,
                                                @RequestParam(required = false) Long communityId) {
        PageResult<OpinionBoxVO> result = opinionBoxService.getOpinionBoxPage(query,
                SecurityContextHolder.getCompanyId(), communityId);
        return Result.success(result);
    }

    @PostMapping
    @OperLog(module = "意见箱", businessType = 1, description = "新增意见箱")
    public Result<Void> add(@Valid @RequestBody OpinionBoxDTO dto) {
        opinionBoxService.createBox(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/submits")
    @OperLog(module = "意见箱", businessType = 1, description = "提交意见")
    public Result<Void> submit(@Valid @RequestBody OpinionSubmitDTO dto) {
        opinionBoxService.submitOpinion(dto,
                SecurityContextHolder.getCompanyId(),
                SecurityContextHolder.getUserId(),
                SecurityContextHolder.getUsername());
        return Result.success();
    }

    @GetMapping("/submits")
    @OperLog(module = "意见箱", businessType = 4, description = "查询意见提交")
    public Result<PageResult<OpinionSubmitVO>> submits(PageQuery query,
                                                      @RequestParam(required = false) Long boxId,
                                                      @RequestParam(required = false) Integer status) {
        return Result.success(opinionBoxService.getOpinionSubmitPage(query,
                SecurityContextHolder.getCompanyId(), boxId, status));
    }

    @PostMapping("/submits/{id}/reply")
    @OperLog(module = "意见箱", businessType = 2, description = "回复意见")
    public Result<Void> reply(@PathVariable Long id, @RequestParam String replyContent) {
        opinionBoxService.replyOpinion(id, replyContent,
                SecurityContextHolder.getUserId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/submits/{id}/evaluate")
    @OperLog(module = "意见箱", businessType = 2, description = "满意度评价")
    public Result<Void> evaluate(@PathVariable Long id, @RequestParam Integer satisfaction) {
        opinionBoxService.evaluateOpinion(id, satisfaction);
        return Result.success();
    }

    @PostMapping("/submits/{id}/close")
    @OperLog(module = "意见箱", businessType = 2, description = "关闭意见")
    public Result<Void> close(@PathVariable Long id) {
        opinionBoxService.closeOpinion(id, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "意见箱", businessType = 3, description = "删除意见箱")
    public Result<Void> delete(@PathVariable Long id) {
        opinionBoxService.removeById(id);
        return Result.success();
    }
}