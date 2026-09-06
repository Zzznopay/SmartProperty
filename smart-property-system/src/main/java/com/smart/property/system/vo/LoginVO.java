package com.smart.property.system.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 登录响应VO
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
@Builder
public class LoginVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 访问令牌 */
    private String accessToken;

    /** 刷新令牌 */
    private String refreshToken;

    /** 过期时间(秒) */
    private Long expiresIn;

    /** 用户信息 */
    private UserInfoVO userInfo;

    @Data
    @Builder
    public static class UserInfoVO implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        private Long id;
        private String username;
        private String realName;
        private String avatar;
        private Long companyId;
        private List<String> roles;
        private List<String> permissions;
    }
}
