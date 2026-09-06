package com.smart.property.common.oss.config;

import com.smart.property.common.oss.service.MinioService;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 自动装配
 * <p>
 * 引入该依赖的服务启动时自动创建 MinioClient 和 MinioService Bean，并确保桶存在。
 * 仅在配置了 minio.endpoint 时生效，避免未使用对象存储的服务启动失败。
 *
 * @author zzz
 * @since 2026-07-27
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(OssProperties.class)
@ConditionalOnProperty(prefix = "minio", name = "endpoint")
public class MinioAutoConfiguration implements SmartInitializingSingleton {

    private final OssProperties props;
    private final ApplicationContext applicationContext;

    public MinioAutoConfiguration(OssProperties props, ApplicationContext applicationContext) {
        this.props = props;
        this.applicationContext = applicationContext;
    }

    @Bean
    public MinioClient minioClient() {
        log.info("MinioClient initialized, endpoint={}", props.getEndpoint());
        MinioClient.Builder builder = MinioClient.builder()
                .credentials(props.getAccessKey(), props.getSecretKey())
                .region(props.getRegion());
        // endpoint 既支持完整 URL（含协议/端口），也支持裸主机名
        String endpoint = props.getEndpoint();
        if (endpoint.contains("://")) {
            builder.endpoint(endpoint);
        } else {
            builder.endpoint(endpoint, props.isSecure() ? 443 : 9000, props.isSecure());
        }
        return builder.build();
    }

    @Bean
    public MinioService minioService(MinioClient client) {
        return new MinioService(client, props);
    }

    /**
     * 所有单例实例化完毕后执行，确保 MinioService 已就绪后再调用 ensureBucket()。
     */
    @Override
    public void afterSingletonsInstantiated() {
        try {
            MinioService minioService = applicationContext.getBean(MinioService.class);
            minioService.ensureBucket();
        } catch (Exception e) {
            // 启动时不阻塞，但打印明确日志（MinIO 不可用时）
            log.warn("MinIO bucket initialization failed, will retry on first use: {}", e.getMessage());
        }
    }
}
