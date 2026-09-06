package com.smart.property.system.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.system.service.SysDeptService;
import com.smart.property.system.vo.SysDeptVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 部门管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/system/depts")
@Tag(name = "部门")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService deptService;

    @GetMapping("/tree")
    @OperLog(module = "部门管理", businessType = 4, description = "查询部门树")
    public Result<List<SysDeptVO>> tree() {
        return Result.success(deptService.getDeptTree(SecurityContextHolder.getCompanyId()));
    }
}