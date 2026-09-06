package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.CommitteeMeetingDTO;
import com.smart.property.operation.service.CommitteeMeetingService;
import com.smart.property.operation.vo.CommitteeMeetingVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 业委会会议 Controller
 */
@RestController
@RequestMapping("/api/v1/admin/committee-meetings")
@Tag(name = "业委会会议")
@RequiredArgsConstructor
public class CommitteeMeetingController {

    private final CommitteeMeetingService committeeMeetingService;

    @GetMapping
    @OperLog(module = "业委会会议", businessType = 4, description = "查询会议列表")
    public Result<PageResult<CommitteeMeetingVO>> list(PageQuery query,
                                                       @RequestParam(required = false) Long communityId) {
        return Result.success(committeeMeetingService.getPage(query,
                SecurityContextHolder.getCompanyId(), communityId));
    }

    @GetMapping("/{id}")
    public Result<CommitteeMeetingVO> getById(@PathVariable Long id) {
        return Result.success(committeeMeetingService.getById(id));
    }

    @PostMapping
    @OperLog(module = "业委会会议", businessType = 1, description = "新增会议")
    public Result<Void> add(@Valid @RequestBody CommitteeMeetingDTO dto) {
        committeeMeetingService.create(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "业委会会议", businessType = 2, description = "修改会议")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CommitteeMeetingDTO dto) {
        committeeMeetingService.update(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "业委会会议", businessType = 3, description = "删除会议")
    public Result<Void> delete(@PathVariable Long id) {
        committeeMeetingService.removeById(id);
        return Result.success();
    }
}