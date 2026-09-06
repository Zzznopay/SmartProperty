package com.smart.property.system.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.system.dto.FileDiskDTO;
import com.smart.property.system.service.FileDiskService;
import com.smart.property.system.vo.FileDiskVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/file/disk")
@Tag(name = "文件磁盘")
@RequiredArgsConstructor
public class FileDiskController {

    private final FileDiskService fileDiskService;

    @GetMapping("/list")
    @OperLog(module = "网盘", businessType = 4, description = "查询网盘文件")
    public Result<List<FileDiskVO>> list(@RequestParam(required = false) Long folderId,
                                         @RequestParam(required = false) Long userId) {
        return Result.success(fileDiskService.getDiskFiles(folderId, userId, SecurityContextHolder.getCompanyId()));
    }

    @GetMapping("/page")
    @OperLog(module = "网盘", businessType = 4, description = "分页查询网盘文件")
    public Result<PageResult<FileDiskVO>> page(@ParameterObject PageQuery query,
                                              @RequestParam(required = false) Long folderId,
                                              @RequestParam(required = false) Long userId) {
        return Result.success(fileDiskService.getDiskFilesPage(query, folderId, userId,
                SecurityContextHolder.getCompanyId()));
    }

    @PostMapping("/upload")
    @OperLog(module = "网盘", businessType = 1, description = "上传网盘文件")
    public Result<Void> upload(@Valid @RequestBody FileDiskDTO dto) {
        fileDiskService.createFile(dto, SecurityContextHolder.getCompanyId(),
                SecurityContextHolder.getUserId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "网盘", businessType = 2, description = "修改网盘文件")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody FileDiskDTO dto) {
        fileDiskService.updateFile(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PostMapping("/{id}/share")
    @OperLog(module = "网盘", businessType = 2, description = "分享文件")
    public Result<Void> share(@PathVariable Long id, @RequestBody List<Long> shareUserIds) {
        fileDiskService.shareFile(id, shareUserIds, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "网盘", businessType = 3, description = "删除网盘文件")
    public Result<Void> delete(@PathVariable Long id) {
        fileDiskService.removeById(id);
        return Result.success();
    }
}