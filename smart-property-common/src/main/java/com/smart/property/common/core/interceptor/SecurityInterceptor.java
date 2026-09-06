package com.smart.property.common.core.interceptor;

import com.smart.property.common.core.constant.CommonConstants;
import com.smart.property.common.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 安全拦截器
 * 从请求头中提取用户信息并设置到上下文
 *
 * @author zzz
 * @since 2026-07-25
 */
public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");
        String companyId = request.getHeader("X-Company-Id");

        if (userId != null) {
            Long uid = Long.parseLong(userId);
            SecurityContextHolder.setUserId(uid);
            // 同步写入请求属性，供控制器 @RequestAttribute 解析（网关已校验 JWT 后下发 X- 头）
            request.setAttribute(CommonConstants.USER_ID_ATTR, uid);
        }
        if (username != null) {
            SecurityContextHolder.setUsername(username);
            request.setAttribute(CommonConstants.USERNAME_ATTR, username);
        }
        if (companyId != null) {
            Long cid = Long.parseLong(companyId);
            SecurityContextHolder.setCompanyId(cid);
            request.setAttribute(CommonConstants.COMPANY_ID_ATTR, cid);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        SecurityContextHolder.clear();
    }
}
