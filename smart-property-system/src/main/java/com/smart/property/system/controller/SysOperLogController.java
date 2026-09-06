package com.smart.property.system.controller;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.system.service.SysOperLogService;
import com.smart.property.system.vo.SysOperLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 操作日志控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/system/oper-logs")
@Tag(name = "操作日志")
@RequiredArgsConstructor
public class SysOperLogController {

    private final SysOperLogService operLogService;

    @GetMapping
    public Result<PageResult<SysOperLogVO>> list(PageQuery query) {
        PageResult<SysOperLogVO> result = operLogService.getOperLogPage(query);
        return Result.success(result);
    }

    /** 清空全部操作日志 */
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        operLogService.cleanAll();
        return Result.success();
    }

    /** 删除单条操作日志 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        operLogService.removeLog(id);
        return Result.success();
    }
}