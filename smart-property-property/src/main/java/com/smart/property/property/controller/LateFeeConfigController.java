package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.LateFeeConfigDTO;
import com.smart.property.property.service.LateFeeConfigService;
import com.smart.property.property.vo.LateFeeConfigVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 滞纳金配置控制器
 *
 * @author zzz
 * @since 2026-07-28
 */
@RestController
@RequestMapping("/api/v1/finance/late-fee-configs")
@Tag(name = "滞纳金")
@RequiredArgsConstructor
public class LateFeeConfigController {

    private final LateFeeConfigService lateFeeConfigService;

    @GetMapping
    @OperLog(module = "滞纳金配置", businessType = 4, description = "查询滞纳金配置")
    public Result<PageResult<LateFeeConfigVO>> list(PageQuery query,
                                                    @RequestParam(required = false) Long communityId) {
        return Result.success(lateFeeConfigService.getLateFeeConfigPage(query,
                SecurityContextHolder.getCompanyId(), communityId));
    }

    @PostMapping
    @OperLog(module = "滞纳金配置", businessType = 1, description = "保存滞纳金配置")
    public Result<Void> save(@Valid @RequestBody LateFeeConfigDTO dto) {
        lateFeeConfigService.saveLateFeeConfig(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "滞纳金配置", businessType = 3, description = "删除滞纳金配置")
    public Result<Void> delete(@PathVariable Long id) {
        lateFeeConfigService.removeById(id);
        return Result.success();
    }
}