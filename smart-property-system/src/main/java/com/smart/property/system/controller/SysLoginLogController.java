package com.smart.property.system.controller;

import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.system.service.SysLoginLogService;
import com.smart.property.system.vo.SysLoginLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 登录日志控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/system/login-logs")
@Tag(name = "登录日志")
@RequiredArgsConstructor
public class SysLoginLogController {

    private final SysLoginLogService loginLogService;

    @GetMapping
    public Result<PageResult<SysLoginLogVO>> list(PageQuery query) {
        PageResult<SysLoginLogVO> result = loginLogService.getLoginLogPage(query);
        return Result.success(result);
    }

    /** 清空全部登录日志 */
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        loginLogService.cleanAll();
        return Result.success();
    }

    /** 删除单条登录日志 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        loginLogService.removeLog(id);
        return Result.success();
    }
}