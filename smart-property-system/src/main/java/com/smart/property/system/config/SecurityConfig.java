package com.smart.property.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 密码编码器配置。
 *
 * <p>认证 / 会话 / 鉴权由 Sa-Token 统一提供（网关 SaReactorFilter + 各服务 SaInterceptor），
 * Spring Security 过滤链已移除，仅保留登录密码校验所需的 BCrypt 编码器。</p>
 *
 * @author zzz
 * @since 2026-07-25
 */
@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
