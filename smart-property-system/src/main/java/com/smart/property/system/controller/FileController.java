package com.smart.property.system.controller;

import com.smart.property.common.core.context.SecurityContextHolder;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.domain.Result;
import com.smart.property.common.log.annotation.OperLog;
import com.smart.property.system.dto.ChunkMergeDTO;
import com.smart.property.system.dto.FileQuery;
import com.smart.property.system.service.FileService;
import com.smart.property.system.vo.FileInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件管理控制器
 *
 * @author zzz
 * @since 2026-07-27
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
@Validated
public class FileController {

    private final FileService fileService;

    @Operation(summary = "单文件上传（含秒传）")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<FileInfoVO> upload(@RequestPart("file") MultipartFile file,
                                     @RequestParam(value = "businessType", required = false, defaultValue = "default") String businessType,
                                     @RequestParam(value = "businessId", required = false) Long businessId) {
        FileInfoVO vo = fileService.upload(file, businessType, businessId,
                SecurityContextHolder.getCompanyId(),
                SecurityContextHolder.getUserId(),
                SecurityContextHolder.getUsername());
        return Result.success(vo);
    }

    @Operation(summary = "分片上传（接收一个分片）")
    @PostMapping(value = "/upload/chunk", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Void> uploadChunk(@RequestPart("file") @NotNull MultipartFile chunk,
                                    @RequestParam("chunkIndex") @NotNull @PositiveOrZero Integer chunkIndex,
                                    @RequestParam("totalChunks") @NotNull @Positive Integer totalChunks,
                                    @RequestParam("fileMd5") @NotBlank String fileMd5,
                                    @RequestParam("fileName") @NotBlank String fileName,
                                    @RequestParam(value = "businessType", required = false, defaultValue = "default") String businessType) {
        if (chunkIndex >= totalChunks) {
            throw new com.smart.property.common.core.exception.BusinessException("分片索引超出范围");
        }
        fileService.uploadChunk(chunk, chunkIndex, totalChunks, fileMd5, fileName, businessType,
                SecurityContextHolder.getCompanyId(),
                SecurityContextHolder.getUserId());
        return Result.success();
    }

    @Operation(summary = "分片合并（完成上传）")
    @PostMapping("/upload/chunk/merge")
    public Result<FileInfoVO> mergeChunks(@Valid @RequestBody ChunkMergeDTO dto) {
        FileInfoVO vo = fileService.mergeChunks(dto,
                SecurityContextHolder.getCompanyId(),
                SecurityContextHolder.getUserId(),
                SecurityContextHolder.getUsername());
        return Result.success(vo);
    }

    @Operation(summary = "文件下载（鉴权后由网关转发）")
    @GetMapping("/download/{id}")
    public void download(@PathVariable Long id, HttpServletResponse response) {
        fileService.download(id, response);
    }

    @Operation(summary = "查询文件元信息")
    @GetMapping("/{id}")
    public Result<FileInfoVO> getById(@PathVariable Long id) {
        return Result.success(fileService.getById(id));
    }

    @Operation(summary = "软删除文件")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        fileService.delete(id, SecurityContextHolder.getCompanyId());
        return Result.success();
    }

    @Operation(summary = "分页查询文件列表")
    @GetMapping("/list")
    public Result<PageResult<FileInfoVO>> list(FileQuery query) {
        return Result.success(fileService.list(query, SecurityContextHolder.getCompanyId()));
    }
}