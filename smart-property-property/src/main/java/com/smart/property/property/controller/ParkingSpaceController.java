package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.ParkingSpaceDTO;
import com.smart.property.property.service.ParkingSpaceService;
import com.smart.property.property.vo.ParkingSpaceVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 车位管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/finance/parking-spaces")
@Tag(name = "车位")
@RequiredArgsConstructor
public class ParkingSpaceController {

    private final ParkingSpaceService parkingSpaceService;

    @GetMapping
    @OperLog(module = "车位管理", businessType = 4, description = "查询车位列表")
    public Result<PageResult<ParkingSpaceVO>> list(PageQuery query,
                                                   @RequestParam(required = false) Long communityId,
                                                   @RequestParam(required = false) Integer status) {
        PageResult<ParkingSpaceVO> result = parkingSpaceService.getParkingPage(query,
                SecurityContextHolder.getCompanyId(), communityId, status);
        return Result.success(result);
    }

    @PostMapping
    @OperLog(module = "车位管理", businessType = 1, description = "新增车位")
    public Result<Void> add(@Valid @RequestBody ParkingSpaceDTO dto) {
        parkingSpaceService.createParking(dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/sale")
    @OperLog(module = "车位管理", businessType = 2, description = "车位销售")
    public Result<Void> sale(@PathVariable Long id,
                             @RequestParam Long ownerId,
                             @RequestParam BigDecimal salePrice) {
        parkingSpaceService.saleParking(id, ownerId, salePrice,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/rent")
    @OperLog(module = "车位管理", businessType = 2, description = "车位出租")
    public Result<Void> rent(@PathVariable Long id,
                             @RequestParam Long tenantId,
                             @RequestParam BigDecimal rentPrice,
                             @RequestParam LocalDate startDate,
                             @RequestParam LocalDate endDate) {
        parkingSpaceService.rentParking(id, tenantId, rentPrice, startDate, endDate,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @GetMapping("/{id}")
    @OperLog(module = "车位管理", businessType = 4, description = "查询车位详情")
    public Result<ParkingSpaceVO> getById(@PathVariable Long id) {
        return Result.success(parkingSpaceService.getParkingById(id, SecurityContextHolder.getCompanyId()));
    }

    @PutMapping("/{id}")
    @OperLog(module = "车位管理", businessType = 2, description = "修改车位")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ParkingSpaceDTO dto) {
        parkingSpaceService.updateParking(id, dto, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/release")
    @OperLog(module = "车位管理", businessType = 2, description = "车位释放")
    public Result<Void> release(@PathVariable Long id) {
        parkingSpaceService.releaseParking(id, SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "车位管理", businessType = 3, description = "删除车位")
    public Result<Void> delete(@PathVariable Long id) {
        parkingSpaceService.deleteParking(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }
}