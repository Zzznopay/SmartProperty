package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.SecurityArrangeDTO;
import com.smart.property.operation.service.SecurityArrangeService;
import com.smart.property.operation.vo.SecurityArrangeVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 保安安排控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/operation/security-arranges")
@Tag(name = "保安安排")
@RequiredArgsConstructor
public class SecurityArrangeController {

    private final SecurityArrangeService securityArrangeService;

    @GetMapping
    @OperLog(module = "保安管理", businessType = 4, description = "查询保安安排")
    public Result<PageResult<SecurityArrangeVO>> list(PageQuery query,
                                                     @RequestParam(required = false) Long communityId,
                                                     @RequestParam(required = false) LocalDate arrangeDate) {
        PageResult<SecurityArrangeVO> result = securityArrangeService.getSecurityArrangePage(query,
                SecurityContextHolder.getCompanyId(), communityId, arrangeDate);
        return Result.success(result);
    }

    @PostMapping
    @OperLog(module = "保安管理", businessType = 1, description = "新增保安安排")
    public Result<Void> add(@Valid @RequestBody SecurityArrangeDTO dto) {
        securityArrangeService.createArrange(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/complete")
    @OperLog(module = "保安管理", businessType = 2, description = "完成执勤")
    public Result<Void> complete(@PathVariable Long id) {
        securityArrangeService.completeDuty(id, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "保安管理", businessType = 3, description = "删除保安安排")
    public Result<Void> delete(@PathVariable Long id) {
        securityArrangeService.removeById(id);
        return Result.success();
    }
}