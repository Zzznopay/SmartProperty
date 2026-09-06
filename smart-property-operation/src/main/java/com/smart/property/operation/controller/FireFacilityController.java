package com.smart.property.operation.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.operation.dto.FireFacilityDTO;
import com.smart.property.operation.service.FireFacilityService;
import com.smart.property.operation.vo.FireFacilityVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 消防设施 Controller
 */
@RestController
@RequestMapping("/api/v1/operation/fire-facilities")
@Tag(name = "消防设施")
@RequiredArgsConstructor
public class FireFacilityController {

    private final FireFacilityService fireFacilityService;

    @GetMapping
    @OperLog(module = "消防管理", businessType = 4, description = "查询消防设施")
    public Result<PageResult<FireFacilityVO>> list(PageQuery query,
                                                  @RequestParam(required = false) Long communityId,
                                                  @RequestParam(required = false) Integer facilityType) {
        PageResult<FireFacilityVO> result = fireFacilityService.getFireFacilityPage(query,
                SecurityContextHolder.getCompanyId(), communityId, facilityType);
        return Result.success(result);
    }

    @PostMapping
    @OperLog(module = "消防管理", businessType = 1, description = "新增消防设施")
    public Result<Void> add(@Valid @RequestBody FireFacilityDTO dto) {
        fireFacilityService.createFacility(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/check")
    @OperLog(module = "消防管理", businessType = 2, description = "检查消防设施")
    public Result<Void> check(@PathVariable Long id) {
        fireFacilityService.checkFacility(id, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "消防管理", businessType = 3, description = "删除消防设施")
    public Result<Void> delete(@PathVariable Long id) {
        fireFacilityService.removeById(id);
        return Result.success();
    }
}