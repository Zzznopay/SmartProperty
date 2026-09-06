package com.smart.property.common.oss.constants;

/**
 * 对象存储相关常量
 *
 * @author zzz
 * @since 2026-07-27
 */
public final class OssConstants {

    private OssConstants() {
    }

    /** 默认存储桶 */
    public static final String DEFAULT_BUCKET = "smartproperty";

    /** 默认分片大小 5 MiB */
    public static final long DEFAULT_CHUNK_SIZE = 5L * 1024 * 1024;

    /** 默认分片上传阈值 15 MiB */
    public static final long DEFAULT_MULTIPART_THRESHOLD = 15L * 1024 * 1024;

    /** Redis 分片元数据 key 前缀 */
    public static final String CHUNK_META_KEY_PREFIX = "chunk:meta:";

    /** Redis 分片接收记录 key 前缀 */
    public static final String CHUNK_RECEIVED_KEY_PREFIX = "chunk:";

    /** 分片临时目录名 */
    public static final String CHUNK_TMP_DIR = "chunk-tmp";

    /** 合并临时文件目录名 */
    public static final String CHUNK_MERGED_DIR = "merged";

    /** 错误码：B 类系统端 - 对象存储错误 */
    public static final String OSS_ERROR_CODE = "B0401";
}
