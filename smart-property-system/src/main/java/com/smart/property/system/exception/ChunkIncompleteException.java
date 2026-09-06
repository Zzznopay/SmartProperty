package com.smart.property.system.exception;

import com.smart.property.common.core.exception.BusinessException;

/**
 * 分片不完整异常
 *
 * @author zzz
 * @since 2026-07-27
 */
public class ChunkIncompleteException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public ChunkIncompleteException(String message) {
        super("B0403", message);
    }
}
