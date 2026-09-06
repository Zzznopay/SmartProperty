package com.smart.property.gateway.config;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import com.smart.property.gateway.filter.AuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sa-Token 网关统一鉴权配置。
 *
 * <p>依据官方文档 micro/gateway-auth 实现：注册 SaReactorFilter 全局过滤器，
 * 校验登录态与角色权限；会话数据通过 sa-token-redis-template 存 Redis，
 * 与各业务服务共享（登录动作在 smart-property-system 完成）。</p>
 *
 * <p>鉴权通过后把身份写入 SaStorage（exchange attributes），
 * 由 {@link AuthFilter} 转发为下游服务的 X-User-Id 等请求头。</p>
 *
 * @author zzz
 * @since 2026-09-06
 */
@Configuration
public class SaTokenConfigure {

    /** 无需登录即可访问的路径（与原网关白名单一致） */
    private static final String[] WHITE_LIST = {
            "/api/v1/auth/login",
            "/api/v1/auth/captcha",
            "/api/v1/auth/sms-code",
            "/api/v1/auth/refresh",
            // Knife4j 聚合文档（演示环境允许匿名查看）
            "/doc.html",
            "/doc.html/",
            "/webjars/",
            "/swagger-resources/",
            "/swagger-ui/",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/v3/api-docs/",
            "/favicon.ico"
    };

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截全部路由
                .addInclude("/**")
                // 放行认证白名单
                .addExclude(WHITE_LIST)
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // 跨域预检请求不携带 Authorization，直接放行
                    String method = SaHolder.getRequest().getMethod();
                    if (method == null || "OPTIONS".equalsIgnoreCase(method)) {
                        return;
                    }

                    // 登录校验：Token 必须有效（未过期、未注销、未被踢下线/顶下线）
                    StpUtil.checkLogin();

                    // 系统管理模块仅 admin 角色可访问（与前端 canAdmin 门槛一致）
                    SaRouter.match("/api/v1/system/**", r -> StpUtil.checkRole("admin"));

                    // 身份信息写入 SaStorage，供 AuthFilter 转发下游请求头
                    Object loginId = StpUtil.getLoginId();
                    SaSession session = StpUtil.getSessionByLoginId(loginId, false);
                    SaHolder.getStorage()
                            .set(AuthFilter.ATTR_USER_ID, loginId)
                            .set(AuthFilter.ATTR_USERNAME, session != null ? session.get("username") : null)
                            .set(AuthFilter.ATTR_COMPANY_ID, session != null ? session.get("companyId") : null);
                })
                // 异常处理：校验失败时按项目统一响应格式返回（HTTP 401/403）
                .setError(e -> {
                    int status = 401;
                    String code = "A0301";
                    String message = "未登录或登录已失效";
                    if (e instanceof NotRoleException || e instanceof NotPermissionException) {
                        status = 403;
                        code = "A0302";
                        message = "无权访问该资源";
                    } else if (e instanceof NotLoginException nle) {
                        message = switch (nle.getType()) {
                            case NotLoginException.KICK_OUT -> "已被踢下线";
                            case NotLoginException.BE_REPLACED -> "已被顶下线";
                            case NotLoginException.TOKEN_FREEZE -> "登录已被冻结";
                            default -> "未登录或登录已失效";
                        };
                    }
                    SaHolder.getResponse().setStatus(status);
                    return "{\"code\":\"" + code + "\",\"message\":\"" + message
                            + "\",\"timestamp\":" + System.currentTimeMillis() + "}";
                });
    }
}
