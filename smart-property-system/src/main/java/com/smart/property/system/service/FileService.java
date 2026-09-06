package com.smart.property.system.service;

import com.smart.property.common.core.domain.PageResult;
import com.smart.property.system.dto.ChunkMergeDTO;
import com.smart.property.system.dto.FileQuery;
import com.smart.property.system.vo.FileInfoVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务
 *
 * @author zzz
 * @since 2026-07-27
 */
public interface FileService {

    /**
     * 单文件上传（含秒传）
     */
    FileInfoVO upload(MultipartFile file, String businessType, Long businessId,
                      Long companyId, Long uploadUserId, String uploadUserName);

    /**
     * 接收一个分片
     */
    void uploadChunk(MultipartFile chunk, int chunkIndex, int totalChunks,
                     String fileMd5, String fileName, String businessType,
                     Long companyId, Long uploadUserId);

    /**
     * 合并分片 + 上传 MinIO
     */
    FileInfoVO mergeChunks(@Valid ChunkMergeDTO dto, Long companyId,
                           Long uploadUserId, String uploadUserName);

    /**
     * 流式下载（鉴权后由网关转发）
     */
    void download(Long id, HttpServletResponse response);

    /**
     * 查询文件元信息
     */
    FileInfoVO getById(Long id);

    /**
     * 软删除（仅本人本租户）
     */
    void delete(Long id, Long companyId);

    /**
     * 分页查询
     */
    PageResult<FileInfoVO> list(FileQuery query, Long companyId);
}
