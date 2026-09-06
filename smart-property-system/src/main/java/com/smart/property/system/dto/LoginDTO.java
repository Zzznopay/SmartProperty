package com.smart.property.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录请求DTO
 *
 * @author zzz
 * @since 2026-07-25
 */
@Data
public class LoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    /** 验证码 */
    private String captchaCode;

    /** 验证码key */
    private String captchaKey;
}
