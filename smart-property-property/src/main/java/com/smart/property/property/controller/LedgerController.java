package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.LedgerDTO;
import com.smart.property.property.service.LedgerService;
import com.smart.property.property.vo.LedgerVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 物业费台帐控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/finance/ledgers")
@Tag(name = "台账")
@RequiredArgsConstructor
public class LedgerController {

    private final LedgerService ledgerService;

    @GetMapping
    @OperLog(module = "台帐管理", businessType = 4, description = "查询台帐列表")
    public Result<PageResult<LedgerVO>> list(PageQuery query,
                                             @RequestParam(required = false) Long roomId,
                                             @RequestParam(required = false) Long ownerId,
                                             @RequestParam(required = false) Integer status) {
        PageResult<LedgerVO> result = ledgerService.getLedgerPage(query,
                SecurityContextHolder.getCompanyId(), roomId, ownerId, status);
        return Result.success(result);
    }

    @GetMapping("/by-room")
    public Result<List<LedgerVO>> listByRoom(@RequestParam Long roomId) {
        return Result.success(ledgerService.getLedgersByRoomId(roomId, SecurityContextHolder.getCompanyId()));
    }

    @GetMapping("/arrears")
    @OperLog(module = "台帐管理", businessType = 4, description = "查询欠费列表")
    public Result<PageResult<LedgerVO>> arrears(PageQuery query) {
        PageResult<LedgerVO> result = ledgerService.getArrearsPage(query, SecurityContextHolder.getCompanyId());
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @OperLog(module = "台帐管理", businessType = 4, description = "查询台帐详情")
    public Result<LedgerVO> getById(@PathVariable Long id) {
        return Result.success(ledgerService.getLedgerById(id, SecurityContextHolder.getCompanyId()));
    }

    @PutMapping("/{id}")
    @OperLog(module = "台帐管理", businessType = 2, description = "修改台帐")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody LedgerDTO dto) {
        ledgerService.updateLedger(id, dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "台帐管理", businessType = 3, description = "删除台帐")
    public Result<Void> delete(@PathVariable Long id) {
        ledgerService.deleteLedger(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }

    @PostMapping("/generate")
    @OperLog(module = "台帐管理", businessType = 1, description = "生成物业费")
    public Result<Void> generate(@RequestParam Long communityId,
                                 @RequestParam Long roomId,
                                 @RequestParam String month) {
        ledgerService.generatePropertyFee(communityId, roomId, month,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }
}