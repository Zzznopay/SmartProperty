package com.smart.property.common.oss.service;

import com.smart.property.common.oss.config.OssProperties;
import com.smart.property.common.oss.exception.OssException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * MinIO 服务封装
 *
 * @author zzz
 * @since 2026-07-27
 */
@Slf4j
public class MinioService {

    private final MinioClient client;
    private final OssProperties props;

    @Autowired
    public MinioService(MinioClient client, OssProperties props) {
        this.client = client;
        this.props = props;
    }

    /**
     * 启动时调用：确保桶存在
     */
    public void ensureBucket() {
        String bucket = props.getBucket();
        try {
            boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                log.info("MinIO bucket created: {}", bucket);
            } else {
                log.info("MinIO bucket exists: {}", bucket);
            }
        } catch (Exception e) {
            throw new OssException("初始化桶失败: " + bucket, e);
        }
    }

    /**
     * 上传对象
     */
    public void putObject(String bucket, String key, InputStream stream, long size, String contentType) {
        try {
            client.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .stream(stream, size, -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            throw new OssException("上传对象失败: " + key, e);
        }
    }

    /**
     * 获取对象输入流（调用方负责关闭）
     */
    public InputStream getObject(String bucket, String key) {
        try {
            return client.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build());
        } catch (Exception e) {
            throw new OssException("获取对象失败: " + key, e);
        }
    }

    /**
     * 获取对象元信息（用于下载时设置 Content-Type / Content-Length）
     */
    public StatObjectResponse statObject(String bucket, String key) {
        try {
            return client.statObject(StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build());
        } catch (ErrorResponseException e) {
            throw new OssException("对象不存在: " + key, e);
        } catch (Exception e) {
            throw new OssException("查询对象元信息失败: " + key, e);
        }
    }

    /**
     * 删除对象
     */
    public void removeObject(String bucket, String key) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(key)
                    .build());
        } catch (Exception e) {
            throw new OssException("删除对象失败: " + key, e);
        }
    }

    /**
     * 生成预签名下载 URL（v2 留待）
     */
    public String presignedGetObject(String bucket, String key, int expireSeconds) {
        try {
            return client.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(key)
                    .expiry(expireSeconds, TimeUnit.SECONDS)
                    .build());
        } catch (Exception e) {
            throw new OssException("生成预签名URL失败: " + key, e);
        }
    }

    public String getBucketName() {
        return props.getBucket();
    }
}
