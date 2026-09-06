package com.smart.property.common.oss.exception;

import com.smart.property.common.core.exception.BusinessException;
import com.smart.property.common.oss.constants.OssConstants;

/**
 * 对象存储异常
 *
 * @author zzz
 * @since 2026-07-27
 */
public class OssException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public OssException(String message) {
        super(OssConstants.OSS_ERROR_CODE, message);
    }

    public OssException(String message, Throwable cause) {
        super(message, cause);
    }
}
