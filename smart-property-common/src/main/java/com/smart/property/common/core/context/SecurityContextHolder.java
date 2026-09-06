package com.smart.property.common.core.context;

import com.smart.property.common.core.exception.BusinessException;

/**
 * 安全上下文持有者
 * 用于在Service层获取当前用户信息。
 *
 * <p>由网关在 JWT 校验通过后下发的 X-User-Id / X-Username / X-Company-Id 三个 header
 * 在 SecurityInterceptor.preHandle 阶段写入，afterCompletion 自动清理。</p>
 *
 * <p>getXxx() 抛错版本：调用方省略 null 校验。
 * getXxxOrNull() 变体：登录态/公开接口允许上下文缺失。</p>
 *
 * @author zzz
 * @since 2026-07-25
 */
public final class SecurityContextHolder {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<Long> COMPANY_ID = new ThreadLocal<>();

    private static final String UNAUTHORIZED_MESSAGE = "未提供有效的认证令牌";

    private SecurityContextHolder() {
    }

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static void setUsername(String username) {
        USERNAME.set(username);
    }

    public static void setCompanyId(Long companyId) {
        COMPANY_ID.set(companyId);
    }

    /**
     * 获取当前用户ID，未登录时抛业务异常。
     */
    public static Long getUserId() {
        Long value = USER_ID.get();
        if (value == null) {
            throw new BusinessException(UNAUTHORIZED_MESSAGE);
        }
        return value;
    }

    /**
     * 获取当前用户ID，未登录时返回 null。仅登录态/公开接口使用。
     */
    public static Long getUserIdOrNull() {
        return USER_ID.get();
    }

    /**
     * 获取当前用户名，未登录时抛业务异常。
     */
    public static String getUsername() {
        String value = USERNAME.get();
        if (value == null) {
            throw new BusinessException(UNAUTHORIZED_MESSAGE);
        }
        return value;
    }

    /**
     * 获取当前用户名，未登录时返回 null。仅登录态/公开接口使用。
     */
    public static String getUsernameOrNull() {
        return USERNAME.get();
    }

    /**
     * 获取当前公司ID，未登录时抛业务异常。
     */
    public static Long getCompanyId() {
        Long value = COMPANY_ID.get();
        if (value == null) {
            throw new BusinessException(UNAUTHORIZED_MESSAGE);
        }
        return value;
    }

    /**
     * 获取当前公司ID，未登录时返回 null。仅登录态/公开接口使用。
     */
    public static Long getCompanyIdOrNull() {
        return COMPANY_ID.get();
    }

    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        COMPANY_ID.remove();
    }
}