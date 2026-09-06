package com.smart.property.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件元信息
 *
 * @author zzz
 * @since 2026-07-27
 */
@Data
@TableName("file_info")
public class FileInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
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

    /** 存储类型(1MinIO 2OSS) */
    private Integer storageType;

    private String bucketName;

    private String businessType;

    private Long businessId;

    private Long uploadUserId;

    private String uploadUserName;

    private Integer downloadCount;

    private Integer status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
