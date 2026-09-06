package com.smart.property.operation.remote.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件上传远程响应 VO（operation 端）
 *
 * @author zzz
 * @since 2026-07-27
 */
@Data
public class FileRemoteUploadVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String fileName;
    private String originalName;
    private String filePath;
    private String fileUrl;
    private Long fileSize;
    private String fileType;
    private String fileExt;
    private String md5;
    private String businessType;
    private Long businessId;
}
