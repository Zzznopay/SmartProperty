package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.SurveyDTO;
import com.smart.property.operation.service.SurveyService;
import com.smart.property.operation.vo.SurveyOptionVO;
import com.smart.property.operation.vo.SurveyStatisticsVO;
import com.smart.property.operation.vo.SurveyVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 投票调查 Controller
 */
@RestController
@RequestMapping("/api/v1/admin/surveys")
@Tag(name = "投票调查")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @GetMapping
    @OperLog(module = "投票调查", businessType = 4, description = "查询投票调查")
    public Result<PageResult<SurveyVO>> list(PageQuery query,
                                            @RequestParam(required = false) Long communityId,
                                            @RequestParam(required = false) Integer status) {
        PageResult<SurveyVO> result = surveyService.getSurveyPage(query,
                SecurityContextHolder.getCompanyId(), communityId, status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @OperLog(module = "投票调查", businessType = 4, description = "查询调查详情")
    public Result<SurveyVO> getById(@PathVariable Long id) {
        return Result.success(surveyService.getByIdVO(id));
    }

    @GetMapping("/{id}/options")
    public Result<List<SurveyOptionVO>> getOptions(@PathVariable Long id) {
        return Result.success(surveyService.getOptions(id));
    }

    @PostMapping
    @OperLog(module = "投票调查", businessType = 1, description = "新增投票调查")
    public Result<Void> add(@Valid @RequestBody SurveyDTO dto) {
        surveyService.createSurvey(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/vote")
    @OperLog(module = "投票调查", businessType = 2, description = "投票")
    public Result<Void> vote(@PathVariable Long id, @RequestParam Long optionId) {
        surveyService.vote(id, optionId,
                SecurityContextHolder.getUserId(),
                SecurityContextHolder.getCompanyId());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "投票调查", businessType = 3, description = "删除投票调查")
    public Result<Void> delete(@PathVariable Long id) {
        surveyService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}/statistics")
    @OperLog(module = "投票调查", businessType = 4, description = "投票统计")
    public Result<List<SurveyStatisticsVO>> statistics(@PathVariable Long id) {
        return Result.success(surveyService.getSurveyStatistics(id));
    }

    @PostMapping("/{id}/finish")
    @OperLog(module = "投票调查", businessType = 2, description = "结束投票")
    public Result<Void> finish(@PathVariable Long id) {
        surveyService.finishSurvey(id, SecurityContextHolder.getUsername());
        return Result.success();
    }
}