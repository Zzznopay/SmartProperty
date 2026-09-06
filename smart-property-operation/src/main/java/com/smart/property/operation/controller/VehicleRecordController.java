package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.VehicleRecordDTO;
import com.smart.property.operation.service.VehicleRecordService;
import com.smart.property.operation.vo.VehicleRecordVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 车辆进出控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/operation/vehicle-records")
@Tag(name = "车辆进出")
@RequiredArgsConstructor
public class VehicleRecordController {

    private final VehicleRecordService vehicleRecordService;

    @GetMapping
    @OperLog(module = "停车管理", businessType = 4, description = "查询车辆记录")
    public Result<PageResult<VehicleRecordVO>> list(PageQuery query,
                                                   @RequestParam(required = false) Long communityId,
                                                   @RequestParam(required = false) Integer recordType) {
        PageResult<VehicleRecordVO> result = vehicleRecordService.getVehiclePage(query,
                SecurityContextHolder.getCompanyId(), communityId, recordType);
        return Result.success(result);
    }

    @PostMapping("/entry")
    @OperLog(module = "停车管理", businessType = 1, description = "车辆入场")
    public Result<Void> entry(@Valid @RequestBody VehicleRecordDTO dto) {
        vehicleRecordService.vehicleEntry(dto, SecurityContextHolder.getCompanyId());
        return Result.success();
    }

    @PostMapping("/{id}/exit")
    @OperLog(module = "停车管理", businessType = 2, description = "车辆出场")
    public Result<Void> exit(@PathVariable Long id) {
        vehicleRecordService.vehicleExit(id, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/pay")
    @OperLog(module = "停车管理", businessType = 2, description = "临时车缴费")
    public Result<Void> pay(@PathVariable Long id) {
        vehicleRecordService.payFee(id, SecurityContextHolder.getUsername());
        return Result.success();
    }
}