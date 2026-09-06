package com.smart.property.system.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 图形验证码响应
 *
 * @author zzz
 * @since 2026-07-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 验证码 key（前端登录时回传，用于服务端比对） */
    private String captchaKey;

    /** base64 编码的图片（data:image/png;base64,xxx） */
    private String img;
}
