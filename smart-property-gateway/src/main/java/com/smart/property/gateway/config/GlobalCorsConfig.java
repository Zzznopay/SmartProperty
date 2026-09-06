package com.smart.property.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * 网关全局 CORS 配置
 * <p>
 * 前端通过绝对地址 http://localhost:8000/api/v1 调用网关，需要网关对常见本地来源放行。
 * 顺序设置在 AuthFilter（Ordered.HIGHEST_PRECEDENCE-100）之后，确保预检 OPTIONS
 * 不被认证拦截，且预检响应携带 CORS 头。
 *
 * @author zzz
 * @since 2026-07-27
 */
@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许凭证（前端读取 token / 自定义头需要）
        config.setAllowCredentials(true);
        // 本地开发常见来源；生产环境应改为实际前端域名
        config.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:3000"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        // 前端拦截器读取的响应头（如有自定义）
        config.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
