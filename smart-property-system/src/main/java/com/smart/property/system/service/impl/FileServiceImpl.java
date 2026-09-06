package com.smart.property.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.property.common.core.domain.PageResult;
import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.common.oss.service.MinioService;
import com.smart.property.common.oss.util.FileTypeDetector;
import com.smart.property.system.convert.FileInfoConverter;
import com.smart.property.system.domain.FileInfo;
import com.smart.property.system.dto.ChunkMergeDTO;
import com.smart.property.system.dto.FileQuery;
import com.smart.property.system.exception.ChunkIncompleteException;
import com.smart.property.system.exception.FileNotFoundException;
import com.smart.property.system.mapper.FileInfoMapper;
import com.smart.property.system.service.FileService;
import com.smart.property.system.service.helper.ChunkMerger;
import com.smart.property.system.service.helper.FilePathResolver;
import com.smart.property.system.service.helper.Md5Deduper;
import com.smart.property.system.vo.FileInfoVO;
import io.minio.StatObjectResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 文件服务实现
 *
 * @author zzz
 * @since 2026-07-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioService minioService;
    private final FileInfoMapper fileInfoMapper;
    private final FilePathResolver filePathResolver;
    private final ChunkMerger chunkMerger;
    private final Md5Deduper md5Deduper;
    private final FileInfoConverter fileInfoConverter;

    @Value("${file.max-single-file-size:104857600}")
    private long maxSingleFileSize;

    @Value("${file.max-chunk-file-size:10485760}")
    private long maxChunkFileSize;

    @Value("${file.chunk-meta-ttl-hours:24}")
    private int chunkMetaTtlHours;

    @Override
    public FileInfoVO upload(MultipartFile file, String businessType, Long businessId,
                             Long companyId, Long uploadUserId, String uploadUserName) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        if (file.getSize() > maxSingleFileSize) {
            throw new BusinessException("文件超过单文件上限: " + maxSingleFileSize + " 字节，请使用分片上传");
        }

        // 秒传：先按文件原始内容算 MD5
        String md5 = computeMd5(file);
        FileInfoVO dedup = md5Deduper.tryDedup(companyId, businessType, md5);
        if (dedup != null) {
            log.info("秒传命中: companyId={}, md5={}, fileId={}", companyId, md5, dedup.getId());
            return dedup;
        }

        FileInfo saved = filePathResolver.uploadAndSave(file, businessType, businessId,
                companyId, uploadUserId, uploadUserName);
        return fileInfoConverter.toVO(saved);
    }

    @Override
    public void uploadChunk(MultipartFile chunk, int chunkIndex, int totalChunks,
                            String fileMd5, String fileName, String businessType,
                            Long companyId, Long uploadUserId) {
        if (chunk == null || chunk.isEmpty()) {
            throw new BusinessException("分片不能为空");
        }
        if (chunkIndex < 0 || chunkIndex >= totalChunks) {
            throw new BusinessException("分片索引越界: index=" + chunkIndex + ", total=" + totalChunks);
        }
        if (chunk.getSize() > maxChunkFileSize) {
            throw new BusinessException("分片超过单片上限: " + maxChunkFileSize + " 字节");
        }

        // 落本地
        Path target = chunkMerger.resolveChunkPath(companyId, fileMd5, chunkIndex);
        try {
            Files.createDirectories(target.getParent());
            chunk.transferTo(target.toFile());
        } catch (IOException e) {
            throw new BusinessException("保存分片失败: " + e.getMessage(), e);
        }

        // Redis 记录
        chunkMerger.markChunk(fileMd5, chunkIndex, chunkMetaTtlHours);
        if (!chunkMerger.hasMeta(fileMd5)) {
            chunkMerger.recordMeta(fileMd5, fileName, totalChunks, businessType,
                    companyId, uploadUserId, chunkMetaTtlHours);
        }
    }

    @Override
    public FileInfoVO mergeChunks(@Valid ChunkMergeDTO dto, Long companyId,
                                  Long uploadUserId, String uploadUserName) {
        if (!chunkMerger.hasMeta(dto.getFileMd5())) {
            throw new ChunkIncompleteException("分片元数据不存在或已过期: " + dto.getFileMd5());
        }
        long received = chunkMerger.receivedChunks(dto.getFileMd5());
        if (received != dto.getTotalChunks()) {
            throw new ChunkIncompleteException("分片不完整: 已接收=" + received + ", 期望=" + dto.getTotalChunks());
        }

        // 秒传检查
        FileInfoVO dedup = md5Deduper.tryDedup(companyId, dto.getBusinessType(), dto.getFileMd5());
        if (dedup != null) {
            chunkMerger.cleanup(companyId, dto.getFileMd5());
            return dedup;
        }

        Path merged = chunkMerger.merge(companyId, dto.getFileMd5(), dto.getTotalChunks());
        try {
            String ext = FileTypeDetector.extOf(dto.getFileName());
            String mime = FileTypeDetector.mimeOf(dto.getFileName());
            FileInfo saved = filePathResolver.uploadTempFileAndSave(merged,
                    dto.getBusinessType(), dto.getBusinessId(),
                    dto.getFileName(), dto.getFileMd5(), mime, ext,
                    companyId, uploadUserId, uploadUserName);
            chunkMerger.cleanup(companyId, dto.getFileMd5());
            return fileInfoConverter.toVO(saved);
        } finally {
            // 出错也清理
            try {
                Files.deleteIfExists(merged);
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void download(Long id, HttpServletResponse response) {
        FileInfo info = fileInfoMapper.selectById(id);
        if (info == null || info.getStatus() != 1) {
            throw new FileNotFoundException("文件不存在或已删除: id=" + id);
        }
        StatObjectResponse stat = minioService.statObject(info.getBucketName(), info.getFilePath().substring(1));
        response.setContentType(stat.contentType() == null ? "application/octet-stream" : stat.contentType());
        response.setContentLengthLong(stat.size());
        response.setHeader("Content-Disposition", "inline;filename=\"" +
                (info.getOriginalName() == null ? "download" : info.getOriginalName()) + "\"");

        try (InputStream in = minioService.getObject(info.getBucketName(), info.getFilePath().substring(1));
             OutputStream out = response.getOutputStream()) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) {
                out.write(buf, 0, n);
            }
            out.flush();
        } catch (IOException e) {
            log.error("下载流出错: fileId={}", id, e);
        }
        // 累加下载次数
        fileInfoMapper.incrementDownloadCount(id);
    }

    @Override
    public FileInfoVO getById(Long id) {
        FileInfo info = fileInfoMapper.selectById(id);
        if (info == null) {
            throw new FileNotFoundException("文件不存在: id=" + id);
        }
        return fileInfoConverter.toVO(info);
    }

    @Override
    public void delete(Long id, Long companyId) {
        FileInfo info = fileInfoMapper.selectById(id);
        if (info == null || info.getStatus() != 1) {
            throw new FileNotFoundException("文件不存在: id=" + id);
        }
        if (companyId != null && !companyId.equals(info.getCompanyId())) {
            throw new BusinessException("无权删除该文件");
        }
        FileInfo update = new FileInfo();
        update.setId(id);
        update.setStatus(2); // 已删除
        fileInfoMapper.updateById(update);
    }

    @Override
    public PageResult<FileInfoVO> list(FileQuery query, Long companyId) {
        Page<FileInfo> page = new Page<>(query.getPageNum(), query.getPageSize());
        LambdaQueryWrapper<FileInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileInfo::getCompanyId, companyId)
                .eq(query.getBusinessType() != null && !query.getBusinessType().isBlank(),
                        FileInfo::getBusinessType, query.getBusinessType())
                .eq(query.getBusinessId() != null, FileInfo::getBusinessId, query.getBusinessId())
                .like(query.getOriginalName() != null && !query.getOriginalName().isBlank(),
                        FileInfo::getOriginalName, query.getOriginalName())
                .orderByDesc(FileInfo::getCreateTime);
        Page<FileInfo> result = fileInfoMapper.selectPage(page, wrapper);
        return new PageResult<>(result.getTotal(), result.getCurrent(), result.getSize(),
                result.getRecords().stream().map(FileInfoVO::from).toList());
    }

    private String computeMd5(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) {
                md.update(buf, 0, n);
            }
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new BusinessException("计算 MD5 失败: " + e.getMessage(), e);
        }
    }
}
