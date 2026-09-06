package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.CommunityDTO;
import com.smart.property.property.service.CommunityService;
import com.smart.property.property.vo.CommunityVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 小区管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/property/communitys")
@Tag(name = "小区")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping
    @OperLog(module = "小区管理", businessType = 4, description = "查询小区列表")
    public Result<PageResult<CommunityVO>> list(PageQuery query) {
        PageResult<CommunityVO> result = communityService.getCommunityPage(query,
                SecurityContextHolder.getCompanyId());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @OperLog(module = "小区管理", businessType = 4, description = "查询小区详情")
    public Result<CommunityVO> getById(@PathVariable Long id) {
        return Result.success(communityService.getCommunityById(id, SecurityContextHolder.getCompanyId()));
    }

    @PostMapping
    @OperLog(module = "小区管理", businessType = 1, description = "新增小区")
    public Result<Void> add(@Valid @RequestBody CommunityDTO dto) {
        communityService.createCommunity(dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "小区管理", businessType = 2, description = "修改小区")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CommunityDTO dto) {
        communityService.updateCommunity(id, dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "小区管理", businessType = 3, description = "删除小区")
    public Result<Void> delete(@PathVariable Long id) {
        communityService.deleteCommunity(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }
}