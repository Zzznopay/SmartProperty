package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.FeeItemDTO;
import com.smart.property.property.dto.LadderConfigDTO;
import com.smart.property.property.service.FeeItemService;
import com.smart.property.property.vo.FeeItemVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 费项管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/finance/fee-items")
@Tag(name = "费项")
@RequiredArgsConstructor
public class FeeItemController {

    private final FeeItemService feeItemService;

    @GetMapping
    @OperLog(module = "费项管理", businessType = 4, description = "查询费项列表")
    public Result<PageResult<FeeItemVO>> list(PageQuery query,
                                               @RequestParam(required = false) Long communityId) {
        PageResult<FeeItemVO> result = feeItemService.getFeeItemPage(query,
                SecurityContextHolder.getCompanyId(), communityId);
        return Result.success(result);
    }

    @GetMapping("/by-community")
    public Result<List<FeeItemVO>> listByCommunity(@RequestParam Long communityId) {
        return Result.success(feeItemService.getFeeItemsByCommunityId(communityId,
                SecurityContextHolder.getCompanyId()));
    }

    @PostMapping
    @OperLog(module = "费项管理", businessType = 1, description = "新增费项")
    public Result<Void> add(@Valid @RequestBody FeeItemDTO dto,
                            @RequestParam(required = false) List<LadderConfigDTO> ladders) {
        feeItemService.createFeeItem(dto, ladders,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "费项管理", businessType = 2, description = "修改费项")
    public Result<Void> update(@PathVariable Long id,
                               @Valid @RequestBody FeeItemDTO dto,
                               @RequestParam(required = false) List<LadderConfigDTO> ladders) {
        feeItemService.updateFeeItem(id, dto, ladders,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "费项管理", businessType = 3, description = "删除费项")
    public Result<Void> delete(@PathVariable Long id) {
        feeItemService.deleteFeeItem(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }

    @GetMapping("/{id}")
    @OperLog(module = "费项管理", businessType = 4, description = "查询费项详情")
    public Result<FeeItemVO> getById(@PathVariable Long id) {
        return Result.success(feeItemService.getFeeItemById(id, SecurityContextHolder.getCompanyId()));
    }

    @PostMapping("/{id}/toggle")
    @OperLog(module = "费项管理", businessType = 2, description = "启用/停用费项")
    public Result<Void> toggle(@PathVariable Long id, @RequestParam Integer isActive) {
        feeItemService.toggleActive(id, isActive,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }
}