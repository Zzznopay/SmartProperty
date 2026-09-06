package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.CommitteeMemberDTO;
import com.smart.property.operation.service.CommitteeMemberService;
import com.smart.property.operation.vo.CommitteeMemberVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 业委会成员 Controller
 */
@RestController
@RequestMapping("/api/v1/admin/committee-members")
@Tag(name = "业委会成员")
@RequiredArgsConstructor
public class CommitteeMemberController {

    private final CommitteeMemberService committeeMemberService;

    @GetMapping
    @OperLog(module = "业委会成员", businessType = 4, description = "查询业委会成员")
    public Result<PageResult<CommitteeMemberVO>> list(PageQuery query,
                                                     @RequestParam(required = false) Long communityId) {
        return Result.success(committeeMemberService.getPage(query,
                SecurityContextHolder.getCompanyId(), communityId));
    }

    @GetMapping("/{id}")
    public Result<CommitteeMemberVO> getById(@PathVariable Long id) {
        return Result.success(committeeMemberService.getById(id));
    }

    @PostMapping
    @OperLog(module = "业委会成员", businessType = 1, description = "新增成员")
    public Result<Void> add(@Valid @RequestBody CommitteeMemberDTO dto) {
        committeeMemberService.create(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "业委会成员", businessType = 2, description = "修改成员")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CommitteeMemberDTO dto) {
        committeeMemberService.update(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "业委会成员", businessType = 3, description = "删除成员")
    public Result<Void> delete(@PathVariable Long id) {
        committeeMemberService.removeById(id);
        return Result.success();
    }
}