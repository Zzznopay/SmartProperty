package com.smart.property.common.core.domain;

import com.smart.property.common.core.constant.CommonConstants;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应对象
 *
 * @param <T> 数据类型
 * @author zzz
 * @since 2026-07-25
 */
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 状态码 */
    private String code;

    /** 响应消息 */
    private String message;

    /** 响应数据 */
    private T data;

    /** 时间戳 */
    private long timestamp = System.currentTimeMillis();

    public Result() {
    }

    public Result(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<>(CommonConstants.SUCCESS_CODE, CommonConstants.SUCCESS_MESSAGE, null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(CommonConstants.SUCCESS_CODE, CommonConstants.SUCCESS_MESSAGE, data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(CommonConstants.SUCCESS_CODE, message, data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(CommonConstants.FAIL_CODE, message, null);
    }

    public static <T> Result<T> fail(String code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(CommonConstants.UNAUTHORIZED_CODE, message, null);
    }

    public static <T> Result<T> forbidden(String message) {
        return new Result<>(CommonConstants.FORBIDDEN_CODE, message, null);
    }
}
