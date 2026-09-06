package com.smart.property.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 网关本地 Redis 配置（WebFlux 响应式）。
 *
 * <p>网关不依赖 smart-property-common（避免 servlet 技术栈污染 WebFlux），独立声明响应式 RedisTemplate。
 * value 必须使用 {@link GenericJackson2JsonRedisSerializer}，与 {@code AuthServiceImpl} 经 common
 * {@code RedisUtils}（同为 GenericJackson2JsonRedisSerializer）写入 {@code auth:token:{userId}} 的
 * 序列化方式保持一致——common 会把 String 序列化为带双引号的 JSON 字符串，若网关用 StringRedisSerializer
 * 读取会得到带引号的原始值，与请求 token 比对不等，误判“令牌已失效”。</p>
 */
@Configuration
public class GatewayRedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        RedisSerializationContext<String, Object> ctx = RedisSerializationContext
                .<String, Object>newSerializationContext(stringSerializer)
                .key(stringSerializer)
                .value(jsonSerializer)
                .hashKey(stringSerializer)
                .hashValue(jsonSerializer)
                .build();
        return new ReactiveRedisTemplate<>(factory, ctx);
    }
}