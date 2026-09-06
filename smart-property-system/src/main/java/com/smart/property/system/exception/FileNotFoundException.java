package com.smart.property.system.exception;

import com.smart.property.common.core.exception.BusinessException;

/**
 * 文件不存在异常
 *
 * @author zzz
 * @since 2026-07-27
 */
public class FileNotFoundException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public FileNotFoundException(String message) {
        super("B0402", message);
    }
}
