package com.smart.property.property.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.property.dto.MeterReadingDTO;
import com.smart.property.property.service.MeterReadingService;
import com.smart.property.property.vo.MeterReadingVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 抄表管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/finance/meter-readings")
@Tag(name = "抄表")
@RequiredArgsConstructor
public class MeterReadingController {

    private final MeterReadingService meterReadingService;

    @GetMapping
    @OperLog(module = "抄表管理", businessType = 4, description = "查询抄表记录")
    public Result<PageResult<MeterReadingVO>> list(PageQuery query,
                                                   @RequestParam(required = false) Long roomId,
                                                   @RequestParam(required = false) Integer meterType,
                                                   @RequestParam(required = false) String readingMonth) {
        PageResult<MeterReadingVO> result = meterReadingService.getMeterReadingPage(query,
                SecurityContextHolder.getCompanyId(), roomId, meterType, readingMonth);
        return Result.success(result);
    }

    @PostMapping
    @OperLog(module = "抄表管理", businessType = 1, description = "新增抄表记录")
    public Result<Void> add(@Valid @RequestBody MeterReadingDTO dto) {
        meterReadingService.createMeterReading(dto,
                SecurityContextHolder.getCompanyId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "抄表管理", businessType = 3, description = "删除抄表记录")
    public Result<Void> delete(@PathVariable Long id) {
        meterReadingService.deleteMeterReading(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }
}