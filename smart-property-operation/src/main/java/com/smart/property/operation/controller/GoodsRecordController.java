package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.GoodsRecordDTO;
import com.smart.property.operation.service.GoodsRecordService;
import com.smart.property.operation.vo.GoodsRecordVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 物品出入 Controller
 */
@RestController
@RequestMapping("/api/v1/operation/goods-records")
@Tag(name = "物品登记")
@RequiredArgsConstructor
public class GoodsRecordController {

    private final GoodsRecordService goodsRecordService;

    @GetMapping
    @OperLog(module = "物品出入", businessType = 4, description = "查询物品出入")
    public Result<PageResult<GoodsRecordVO>> list(PageQuery query,
                                                 @RequestParam(required = false) Long communityId) {
        return Result.success(goodsRecordService.getPage(query,
                SecurityContextHolder.getCompanyId(), communityId));
    }

    @GetMapping("/{id}")
    public Result<GoodsRecordVO> getById(@PathVariable Long id) {
        return Result.success(goodsRecordService.getById(id));
    }

    @PostMapping
    @OperLog(module = "物品出入", businessType = 1, description = "登记物品出入")
    public Result<Void> add(@Valid @RequestBody GoodsRecordDTO dto) {
        goodsRecordService.create(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "物品出入", businessType = 3, description = "删除物品记录")
    public Result<Void> delete(@PathVariable Long id) {
        goodsRecordService.removeById(id);
        return Result.success();
    }
}