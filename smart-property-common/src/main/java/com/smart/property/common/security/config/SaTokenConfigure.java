package com.smart.property.common.security.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 鉴权配置（各业务服务）。
 *
 * <p>路由级登录校验在网关已统一执行（SaReactorFilter），本配置为业务服务提供
 * 二道防线（防止绕过网关直连服务），同时开启注解鉴权，
 * 支持在 Controller 上使用 @SaCheckLogin / @SaCheckRole / @SaCheckPermission。</p>
 *
 * @author zzz
 * @since 2026-09-06
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    /** 服务内无需登录即可访问的接口（与网关白名单保持一致） */
    private static final String[] AUTH_WHITELIST = {
            "/api/v1/auth/login",
            "/api/v1/auth/captcha",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout",
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 登录校验：拦截全部业务接口，放行认证白名单
            SaRouter.match("/api/**")
                    .notMatch(AUTH_WHITELIST)
                    .check(r -> StpUtil.checkLogin());

            // 系统管理模块仅 admin 角色可访问（与前端 canAdmin 门槛一致）
            SaRouter.match("/api/v1/system/**", r -> StpUtil.checkRole("admin"));
        })).addPathPatterns("/**");
    }
}
