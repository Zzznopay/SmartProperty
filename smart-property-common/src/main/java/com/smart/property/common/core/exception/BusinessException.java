package com.smart.property.common.core.exception;

import com.smart.property.common.core.constant.CommonConstants;
import lombok.Getter;

/**
 * 业务异常
 *
 * @author zzz
 * @since 2026-07-25
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** 错误码 */
    private final String code;

    public BusinessException(String message) {
        super(message);
        this.code = CommonConstants.FAIL_CODE;
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = CommonConstants.FAIL_CODE;
    }
}
