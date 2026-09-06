package com.smart.property.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 身份信息转发过滤器。
 *
 * <p>登录校验由 Sa-Token 的 {@code SaReactorFilter}（WebFilter，先于本过滤器执行）统一完成，
 * 校验通过后会把身份信息写入 SaStorage（即 exchange attributes）；
 * 本过滤器只负责把这些身份写入下游请求头，供业务服务的
 * {@code SecurityHeaderFilter} 填充 ThreadLocal 上下文。</p>
 *
 * @author zzz
 * @since 2026-07-25
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {

    /** SaReactorFilter 鉴权通过后写入 SaStorage 的身份 key（同时用作 exchange attribute key） */
    public static final String ATTR_USER_ID = "X-User-Id";
    public static final String ATTR_USERNAME = "X-Username";
    public static final String ATTR_COMPANY_ID = "X-Company-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 跨域预检请求直接放行
        if (HttpMethod.OPTIONS.equals(request.getMethod())) {
            return chain.filter(exchange);
        }

        // loginId 经 Sa-Token Redis 序列化后可能为 String，统一按 Object 读取
        Object userId = exchange.getAttribute(ATTR_USER_ID);
        // 白名单请求（登录/验证码/文档等）未经过鉴权流程，无身份信息，原样放行
        if (userId == null) {
            return chain.filter(exchange);
        }

        Object username = exchange.getAttribute(ATTR_USERNAME);
        Object companyId = exchange.getAttribute(ATTR_COMPANY_ID);

        ServerHttpRequest mutatedRequest = request.mutate()
                .header(ATTR_USER_ID, String.valueOf(userId))
                .header(ATTR_USERNAME, username != null ? String.valueOf(username) : "")
                .header(ATTR_COMPANY_ID, companyId != null ? String.valueOf(companyId) : "")
                .build();
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
