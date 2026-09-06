package com.smart.property.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.smart.property.system.domain.FileInfo;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件信息VO
 *
 * @author zzz
 * @since 2026-07-27
 */
@Data
public class FileInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long companyId;
    private String fileName;
    private String originalName;
    private String filePath;
    private String fileUrl;
    private Long fileSize;
    private String fileType;
    private String fileExt;
    private String md5;
    private Integer storageType;
    private String bucketName;
    private String businessType;
    private Long businessId;
    private Long uploadUserId;
    private String uploadUserName;
    private Integer downloadCount;
    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public static FileInfoVO from(FileInfo f) {
        if (f == null) {
            return null;
        }
        FileInfoVO v = new FileInfoVO();
        v.setId(f.getId());
        v.setCompanyId(f.getCompanyId());
        v.setFileName(f.getFileName());
        v.setOriginalName(f.getOriginalName());
        v.setFilePath(f.getFilePath());
        v.setFileUrl(f.getFileUrl());
        v.setFileSize(f.getFileSize());
        v.setFileType(f.getFileType());
        v.setFileExt(f.getFileExt());
        v.setMd5(f.getMd5());
        v.setStorageType(f.getStorageType());
        v.setBucketName(f.getBucketName());
        v.setBusinessType(f.getBusinessType());
        v.setBusinessId(f.getBusinessId());
        v.setUploadUserId(f.getUploadUserId());
        v.setUploadUserName(f.getUploadUserName());
        v.setDownloadCount(f.getDownloadCount());
        v.setStatus(f.getStatus());
        v.setCreateTime(f.getCreateTime());
        return v;
    }
}
