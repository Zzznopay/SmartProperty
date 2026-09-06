package com.smart.property.common.core.constant;

/**
 * 通用常量
 *
 * @author zzz
 * @since 2026-07-25
 */
public final class CommonConstants {

    private CommonConstants() {
    }

    /** 成功状态码 */
    public static final String SUCCESS_CODE = "00000";

    /** 成功消息 */
    public static final String SUCCESS_MESSAGE = "操作成功";

    /** 失败状态码 */
    public static final String FAIL_CODE = "B0001";

    /** 失败消息 */
    public static final String FAIL_MESSAGE = "操作失败";

    /** 未授权状态码 */
    public static final String UNAUTHORIZED_CODE = "A0301";

    /** 禁止访问状态码 */
    public static final String FORBIDDEN_CODE = "A0302";

    /** 请求头 - 租户ID */
    public static final String TENANT_HEADER = "X-Tenant-Id";

    /** 请求头 - Authorization */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /** Token前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** 用户ID请求属性 */
    public static final String USER_ID_ATTR = "userId";

    /** 用户名请求属性 */
    public static final String USERNAME_ATTR = "username";

    /** 公司ID请求属性 */
    public static final String COMPANY_ID_ATTR = "companyId";
}
