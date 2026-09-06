package com.smart.property.common.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 统一 ObjectMapper 配置
 *
 * <p>项目内 Redis 序列化、JSON 转换统一使用本 Bean，避免各处 {@code new ObjectMapper()} 行为不一致。
 * 行为：
 * <ul>
 *     <li>注册 JavaTimeModule（LocalDateTime / LocalDate 与 jackson 互转）</li>
 *     <li>关闭时间戳格式（序列化为 ISO-8601）</li>
 *     <li>激活默认类型（{@code NON_FINAL} + LaissezFaireSubTypeValidator），仅 Redis 缓存对象反序列化时携带类型</li>
 * </ul>
 *
 * <p>使用 {@code @Primary}，web 默认 ObjectMapper 仍由 Spring Boot JacksonAutoConfiguration 提供，
 * 本 Bean 仅作为 Redis / 业务 JSON 场景的"项目级默认"。</p>
 *
 * @author zzz
 * @since 2026-07-31
 */
@Configuration
public class JacksonObjectMapperConfig {

    @Bean(name = "smartPropertyObjectMapper")
    @Primary
    @ConditionalOnMissingBean(name = "smartPropertyObjectMapper")
    public ObjectMapper smartPropertyObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
