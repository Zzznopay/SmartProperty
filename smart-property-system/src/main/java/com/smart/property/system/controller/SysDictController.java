package com.smart.property.system.controller;

import com.smart.property.common.core.domain.Result;
import com.smart.property.system.service.SysDictService;
import com.smart.property.system.vo.SysDictDataVO;
import com.smart.property.system.vo.SysDictTypeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 字典管理控制器
 *
 * @author zzz
 * @since 2026-07-25
 */
@RestController
@RequestMapping("/api/v1/system/dict")
@Tag(name = "字典")
@RequiredArgsConstructor
public class SysDictController {

    private final SysDictService dictService;

    @GetMapping("/types")
    public Result<List<SysDictTypeVO>> listTypes() {
        return Result.success(dictService.getDictTypeList());
    }

    @GetMapping("/data/{dictType}")
    public Result<List<SysDictDataVO>> listData(@PathVariable String dictType) {
        return Result.success(dictService.getDictDataByType(dictType));
    }
}
