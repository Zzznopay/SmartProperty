package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.FireDrillDTO;
import com.smart.property.operation.service.FireDrillService;
import com.smart.property.operation.vo.FireDrillVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 消防演练 Controller
 */
@RestController
@RequestMapping("/api/v1/operation/fire-drills")
@Tag(name = "消防演练")
@RequiredArgsConstructor
public class FireDrillController {

    private final FireDrillService fireDrillService;

    @GetMapping
    @OperLog(module = "消防演练", businessType = 4, description = "查询消防演练")
    public Result<PageResult<FireDrillVO>> list(PageQuery query,
                                                @RequestParam(required = false) Long communityId) {
        return Result.success(fireDrillService.getPage(query,
                SecurityContextHolder.getCompanyId(), communityId));
    }

    @GetMapping("/{id}")
    public Result<FireDrillVO> getById(@PathVariable Long id) {
        return Result.success(fireDrillService.getById(id));
    }

    @PostMapping
    @OperLog(module = "消防演练", businessType = 1, description = "新增消防演练")
    public Result<Void> add(@Valid @RequestBody FireDrillDTO dto) {
        fireDrillService.create(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "消防演练", businessType = 2, description = "修改消防演练")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody FireDrillDTO dto) {
        fireDrillService.update(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "消防演练", businessType = 3, description = "删除消防演练")
    public Result<Void> delete(@PathVariable Long id) {
        fireDrillService.removeById(id);
        return Result.success();
    }
}