package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.FirePatrolDTO;
import com.smart.property.operation.service.FirePatrolService;
import com.smart.property.operation.vo.FirePatrolVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 消防巡查 Controller
 */
@RestController
@RequestMapping("/api/v1/operation/fire-patrols")
@Tag(name = "消防巡查")
@RequiredArgsConstructor
public class FirePatrolController {

    private final FirePatrolService firePatrolService;

    @GetMapping
    @OperLog(module = "消防巡查", businessType = 4, description = "查询消防巡查")
    public Result<PageResult<FirePatrolVO>> list(PageQuery query,
                                                @RequestParam(required = false) Long communityId) {
        return Result.success(firePatrolService.getPage(query,
                SecurityContextHolder.getCompanyId(), communityId));
    }

    @GetMapping("/{id}")
    public Result<FirePatrolVO> getById(@PathVariable Long id) {
        return Result.success(firePatrolService.getById(id));
    }

    @PostMapping
    @OperLog(module = "消防巡查", businessType = 1, description = "新增消防巡查")
    public Result<Void> add(@Valid @RequestBody FirePatrolDTO dto) {
        firePatrolService.create(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "消防巡查", businessType = 2, description = "修改消防巡查")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody FirePatrolDTO dto) {
        firePatrolService.update(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "消防巡查", businessType = 3, description = "删除消防巡查")
    public Result<Void> delete(@PathVariable Long id) {
        firePatrolService.removeById(id);
        return Result.success();
    }
}