package com.smart.property.common.oss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MinIO 配置属性
 *
 * @author zzz
 * @since 2026-07-27
 */
@Data
@ConfigurationProperties(prefix = "minio")
public class OssProperties {

    /** MinIO API endpoint（不含协议） */
    private String endpoint;

    /** access key */
    private String accessKey;

    /** secret key */
    private String secretKey;

    /** 区域 */
    private String region = "us-east-1";

    /** 桶名 */
    private String bucket = "smartproperty";

    /** 是否 https */
    private boolean secure = false;

    /** path-style access（MinIO 必需） */
    private boolean pathStyleAccess = true;

    /** 预签名 URL 过期时间（秒） */
    private int presignedExpireSeconds = 3600;

    /** 分片上传参数 */
    private Multipart multipart = new Multipart();

    public Multipart getMultipart() {
        return multipart;
    }

    public void setMultipart(Multipart multipart) {
        this.multipart = multipart;
    }
}
