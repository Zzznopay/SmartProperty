package com.smart.property.common.security.filter;

import com.smart.property.common.core.context.SecurityContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 安全 Header 过滤器
 *
 * <p>网关在 JWT 校验通过后向请求注入 X-User-Id / X-Username / X-Company-Id 三个 header；
 * 本过滤器在进入业务 Controller 之前把 header 解析后写入
 * {@link SecurityContextHolder}（ThreadLocal），业务层即可通过
 * {@code SecurityContextHolder.getUserId()} 等获取当前操作人；请求结束自动清理。</p>
 *
 * <p>仅对 servlet 业务服务生效（webflux gateway 由自身 AuthFilter 处理）。
 * 未携带 header 时不抛错（公开接口 / 健康检查 / 登录等），交由业务方法自行处理。</p>
 *
 * @author zzz
 * @since 2026-07-31
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class SecurityHeaderFilter extends OncePerRequestFilter {

    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_USERNAME = "X-Username";
    private static final String HEADER_COMPANY_ID = "X-Company-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String userId = request.getHeader(HEADER_USER_ID);
            if (userId != null && !userId.isEmpty()) {
                try {
                    SecurityContextHolder.setUserId(Long.parseLong(userId));
                } catch (NumberFormatException e) {
                    log.warn("X-User-Id 无法解析: {}", userId);
                }
            }
            String username = request.getHeader(HEADER_USERNAME);
            if (username != null && !username.isEmpty()) {
                SecurityContextHolder.setUsername(username);
            }
            String companyId = request.getHeader(HEADER_COMPANY_ID);
            if (companyId != null && !companyId.isEmpty()) {
                try {
                    SecurityContextHolder.setCompanyId(Long.parseLong(companyId));
                } catch (NumberFormatException e) {
                    log.warn("X-Company-Id 无法解析: {}", companyId);
                }
            }
            chain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clear();
        }
    }
}
