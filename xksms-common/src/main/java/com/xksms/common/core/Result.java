package com.xksms.common.core;

import com.xksms.common.enums.GlobalErrorCodeEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 全局统一响应结果封装。
 * <p>
 * 这个类是 xksms-cloud 平台所有 HTTP API 响应的标准化数据结构。
 * 它通过泛型 <T> 支持承载任何类型的业务数据。
 *
 * @param <T> 响应体中包含的业务数据类型
 */
public record Result<T>(int code, String message, T data) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int SUCCESS_CODE = GlobalErrorCodeEnum.SUCCESS.getCode();

    /**
     * 成功响应（无数据）。
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功响应（有数据）。
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, GlobalErrorCodeEnum.SUCCESS.getMessage(), data);
    }

    /**
     * 失败响应（根据错误码）。
     */
    public static <T> Result<T> failure(IErrorCode errorCode) {
        return failure(errorCode, errorCode.getMessage());
    }

    /**
     * 失败响应（根据错误码和自定义消息）。
     */
    public static <T> Result<T> failure(IErrorCode errorCode, String message) {
        return new Result<>(errorCode.getCode(), message, null);
    }

    /**
     * 失败响应（根据自定义错误码和消息）。
     */
    public static <T> Result<T> failure(int code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * 响应是否表示成功。
     */
    public boolean isSuccess() {
        return this.code == SUCCESS_CODE;
    }
}
