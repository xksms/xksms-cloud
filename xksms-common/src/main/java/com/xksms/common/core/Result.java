package com.xksms.common.core;

import com.xksms.common.enums.GlobalErrorCodeEnum;
import lombok.Getter;

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
@Getter
public class Result<T> implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 响应码
	 */
	private final int code;

	/**
	 * 响应消息
	 */
	private final String message;

	/**
	 * 响应数据
	 */
	private final T data;

	// 私有构造函数，强制通过静态工厂方法创建实例
	private Result(int code, String message, T data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	// --- 静态工厂方法 ---

	/**
	 * 成功响应（无数据）
	 */
	public static <T> Result<T> success() {
		return new Result<>(GlobalErrorCodeEnum.SUCCESS.getCode(), GlobalErrorCodeEnum.SUCCESS.getMessage(), null);
	}

	/**
	 * 成功响应（有数据）
	 */
	public static <T> Result<T> success(T data) {
		return new Result<>(GlobalErrorCodeEnum.SUCCESS.getCode(), GlobalErrorCodeEnum.SUCCESS.getMessage(), data);
	}

	/**
	 * 失败响应（根据错误码）
	 */
	public static <T> Result<T> failure(IErrorCode errorCode) {
		return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
	}

	/**
	 * 失败响应（根据错误码和自定义消息）
	 */
	public static <T> Result<T> failure(IErrorCode errorCode, String message) {
		return new Result<>(errorCode.getCode(), message, null);
	}

	/**
	 * 失败响应（根据自定义错误码和消息）
	 */
	public static <T> Result<T> failure(int code, String message) {
		return new Result<>(code, message, null);
	}
}