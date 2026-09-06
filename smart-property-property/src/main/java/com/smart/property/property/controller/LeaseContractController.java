package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.LeaseContractDTO;
import com.smart.property.property.service.LeaseContractService;
import com.smart.property.property.vo.LeaseContractVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 租赁合同控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/property/lease-contracts")
@Tag(name = "租赁合同")
@RequiredArgsConstructor
public class LeaseContractController {

    private final LeaseContractService leaseContractService;

    @GetMapping
    @OperLog(module = "租赁管理", businessType = 4, description = "查询租赁合同")
    public Result<PageResult<LeaseContractVO>> list(PageQuery query,
                                                    @RequestParam(required = false) Integer status) {
        PageResult<LeaseContractVO> result = leaseContractService.getContractPage(query,
                SecurityContextHolder.getCompanyId(), status);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @OperLog(module = "租赁管理", businessType = 4, description = "查询合同详情")
    public Result<LeaseContractVO> getById(@PathVariable Long id) {
        return Result.success(leaseContractService.getContractById(id, SecurityContextHolder.getCompanyId()));
    }

    @PostMapping
    @OperLog(module = "租赁管理", businessType = 1, description = "新增租赁合同")
    public Result<Void> add(@Valid @RequestBody LeaseContractDTO dto) {
        leaseContractService.createContract(dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "租赁管理", businessType = 2, description = "修改租赁合同")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody LeaseContractDTO dto) {
        leaseContractService.updateContract(id, dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/terminate")
    @OperLog(module = "租赁管理", businessType = 2, description = "终止租赁合同")
    public Result<Void> terminate(@PathVariable Long id, @RequestParam String reason) {
        leaseContractService.terminateContract(id, SecurityContextHolder.getCompanyId(), reason, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/activate")
    @OperLog(module = "租赁管理", businessType = 2, description = "激活租赁合同")
    public Result<Void> activate(@PathVariable Long id) {
        leaseContractService.activateContract(id, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/transfer")
    @OperLog(module = "租赁管理", businessType = 2, description = "租户转兑")
    public Result<Void> transfer(@PathVariable Long id, @RequestParam Long newTenantId) {
        leaseContractService.transferContract(id, SecurityContextHolder.getCompanyId(), newTenantId, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @GetMapping("/expiring")
    @OperLog(module = "租赁管理", businessType = 4, description = "查询即将到期合同")
    public Result<PageResult<LeaseContractVO>> expiring(PageQuery query,
                                                       @RequestParam(defaultValue = "30") int days) {
        PageResult<LeaseContractVO> result = leaseContractService.getExpiringContracts(query,
                SecurityContextHolder.getCompanyId(), days);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "租赁管理", businessType = 3, description = "删除租赁合同")
    public Result<Void> delete(@PathVariable Long id) {
        leaseContractService.deleteContract(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }
}