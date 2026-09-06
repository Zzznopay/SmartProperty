package com.smart.property.system.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageQuery;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.system.dto.FileFolderDTO;
import com.smart.property.system.service.FileFolderService;
import com.smart.property.system.vo.FileFolderVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/file/disk/folders")
@Tag(name = "文件夹")
@RequiredArgsConstructor
public class FileFolderController {

    private final FileFolderService fileFolderService;

    @GetMapping("/page")
    @OperLog(module = "网盘", businessType = 4, description = "分页查询文件夹")
    public Result<PageResult<FileFolderVO>> page(@ParameterObject PageQuery query,
                                                @RequestParam(required = false) Long parentId,
                                                @RequestParam(required = false) Long userId) {
        return Result.success(fileFolderService.getFoldersPage(query, parentId, userId,
                SecurityContextHolder.getCompanyId()));
    }

    @GetMapping
    @OperLog(module = "网盘", businessType = 4, description = "查询文件夹")
    public Result<List<FileFolderVO>> list(@RequestParam(required = false) Long parentId,
                                          @RequestParam(required = false) Long userId) {
        return Result.success(fileFolderService.getFolders(parentId, userId, SecurityContextHolder.getCompanyId()));
    }

    @PostMapping
    @OperLog(module = "网盘", businessType = 1, description = "新建文件夹")
    public Result<Void> add(@Valid @RequestBody FileFolderDTO dto) {
        fileFolderService.createFolder(dto, SecurityContextHolder.getCompanyId(),
                SecurityContextHolder.getUserId(), SecurityContextHolder.getUsername());
        return Result.success();
    }

    @PutMapping("/{id}")
    @OperLog(module = "网盘", businessType = 2, description = "修改文件夹")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody FileFolderDTO dto) {
        fileFolderService.updateFolder(id, dto, SecurityContextHolder.getUsername());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @OperLog(module = "网盘", businessType = 3, description = "删除文件夹")
    public Result<Void> delete(@PathVariable Long id) {
        fileFolderService.removeById(id);
        return Result.success();
    }
}