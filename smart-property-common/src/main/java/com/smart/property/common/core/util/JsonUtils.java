package com.smart.property.common.core.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * JSON 工具类
 * <p>统一使用项目 ObjectMapper（RedisConfig 中的 redisObjectMapper Bean），避免业务层各自 new ObjectMapper。</p>
 *
 * <p>使用方式：在 Spring 环境外调用 {@link #toJson(Object)} 时会 lazy-load 一个默认 ObjectMapper；
 * 在 Spring 环境内请通过依赖注入获取 ObjectMapper 后调用 {@link #INSTANCE} 替换或直接使用注入的 mapper。</p>
 *
 * @author zzz
 * @since 2026-07-31
 */
@Slf4j
public final class JsonUtils {

    /**
     * 全局 ObjectMapper，Spring 启动后由 {@link #setMapper(ObjectMapper)} 覆盖为容器内 Bean。
     */
    private static volatile ObjectMapper MAPPER = createDefault();

    private JsonUtils() {
    }

    private static ObjectMapper createDefault() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    public static void setMapper(ObjectMapper mapper) {
        if (mapper != null) {
            MAPPER = mapper;
        }
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSON 序列化失败: {}", e.getMessage(), e);
            return null;
        }
    }

    public static <T> T parse(String json, Class<T> type) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            log.error("JSON 反序列化失败: {}", e.getMessage(), e);
            return null;
        }
    }

    public static <T> T parse(String json, TypeReference<T> type) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            log.error("JSON 反序列化失败: {}", e.getMessage(), e);
            return null;
        }
    }

    public static <T> List<T> parseList(String json, Class<T> elementType) {
        if (json == null || json.isEmpty()) {
            return List.of();
        }
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (Exception e) {
            log.error("JSON 反序列化为 List 失败: {}", e.getMessage(), e);
            return List.of();
        }
    }

    public static Map<String, Object> parseMap(String json) {
        if (json == null || json.isEmpty()) {
            return Map.of();
        }
        try {
            return MAPPER.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            log.error("JSON 反序列化为 Map 失败: {}", e.getMessage(), e);
            return Map.of();
        }
    }
}
