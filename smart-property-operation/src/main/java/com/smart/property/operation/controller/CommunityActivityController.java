package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.CommunityActivityDTO;
import com.smart.property.operation.service.CommunityActivityService;
import com.smart.property.operation.vo.CommunityActivityVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 社区活动控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/operation/community-activities")
@Tag(name = "社区活动")
@RequiredArgsConstructor
public class CommunityActivityController {

    private final CommunityActivityService communityActivityService;

    @GetMapping
    @OperLog(module = "社区活动", businessType = 4, description = "查询社区活动")
    public Result<PageResult<CommunityActivityVO>> list(PageQuery query,
                                                       @RequestParam(required = false) Long communityId,
                                                       @RequestParam(required = false) Integer activityType) {
        PageResult<CommunityActivityVO> result = communityActivityService.getActivityPage(query,
                SecurityContextHolder.getCompanyId(), communityId, activityType);
        return Result.success(result);
    }

    @PostMapping
    @OperLog(module = "社区活动", businessType = 1, description = "新增社区活动")
    public Result<Void> add(@Valid @RequestBody CommunityActivityDTO dto) {
        communityActivityService.createActivity(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "社区活动", businessType = 2, description = "修改社区活动")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CommunityActivityDTO dto) {
        communityActivityService.updateActivity(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/complete")
    @OperLog(module = "社区活动", businessType = 2, description = "完成活动")
    public Result<Void> complete(@PathVariable Long id) {
        communityActivityService.completeActivity(id, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "社区活动", businessType = 3, description = "删除社区活动")
    public Result<Void> delete(@PathVariable Long id) {
        communityActivityService.removeById(id);
        return Result.success();
    }
}