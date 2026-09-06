package com.smart.property.common.security.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Feign 令牌中继拦截器
 *
 * <p>各业务服务共享 Sa-Token 的 Redis 会话，把当前请求的 {@code Authorization}
 * 头原样转发到 Feign 下游请求，跨服务调用即可通过下游的 SaInterceptor
 * 登录校验（二道防线），无需把 internal 路径加入白名单。</p>
 *
 * <p>非 HTTP 请求线程（定时任务、MQ 消费者）无令牌可中继，保持原样——
 * 由调用方 FallbackFactory 空结果兜底。</p>
 *
 * @author zzz
 * @since 2026-09-06
 */
@Component
public class FeignTokenRelayInterceptor implements RequestInterceptor {

    private static final String HEADER_AUTHORIZATION = "Authorization";

    @Override
    public void apply(RequestTemplate template) {
        // 调用方已显式设置认证头时不覆盖
        if (template.headers().containsKey(HEADER_AUTHORIZATION)) {
            return;
        }
        if (!(RequestContextHolder.getRequestAttributes()
                instanceof ServletRequestAttributes attributes)) {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String authorization = request.getHeader(HEADER_AUTHORIZATION);
        if (authorization != null && !authorization.isBlank()) {
            template.header(HEADER_AUTHORIZATION, authorization);
        }
    }
}
