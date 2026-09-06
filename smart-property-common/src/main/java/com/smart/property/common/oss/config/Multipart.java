package com.smart.property.common.oss.config;

import lombok.Data;

/**
 * MinIO 分片上传参数（外部独立类，避免 lombok + Java 25 inner class 字节码兼容问题）
 *
 * @author zzz
 * @since 2026-07-28
 */
@Data
public class Multipart {
    /** 分片大小（字节），默认 5 MiB */
    private long partSizeBytes = 5L * 1024 * 1024;
    /** 切换到分片上传的阈值（字节），默认 15 MiB */
    private long uploadThresholdBytes = 15L * 1024 * 1024;
}
