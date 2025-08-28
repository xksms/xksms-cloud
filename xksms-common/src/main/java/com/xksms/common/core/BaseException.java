package com.xksms.common.core;

import lombok.Getter;

/**
 * xksms-cloud 平台的基础异常类。
 * <p>
 * 它是系统中所有自定义业务异常的父类。
 * 这个类的设计核心，是强制所有业务异常都必须与一个实现了 {@link IErrorCode} 接口的错误码进行关联。
 * 它不关心具体的业务场景，只负责承载和传递结构化的错误信息。
 */
@Getter
public class BaseException extends RuntimeException {

	/**
	 * 错误码对象，包含了 code 和 message
	 */
	private final IErrorCode errorCode;

	/**
	 * 构造函数，强制要求传入一个错误码对象。
	 *
	 * @param errorCode 实现了 IErrorCode 接口的错误码枚举或实例
	 */
	public BaseException(IErrorCode errorCode) {
		// [关键] 我们不再信任和使用父类的 message 字段，
		// 而是将错误信息完全委托给 errorCode 对象来管理。
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	/**
	 * [核心新增]
	 * 允许在抛出异常时，覆盖错误码的默认消息。
	 *
	 * @param errorCode 错误码
	 * @param message   自定义的、更具体的错误消息
	 */
	public BaseException(IErrorCode errorCode, String message) {
		// 构造函数2：使用传入的自定义消息
		super(message);
		this.errorCode = errorCode;
	}

	/**
	 * 允许在抛出基础异常时，包装一个底层的原始异常。
	 * 这对于保留完整的异常堆栈信息、便于问题排查至关重要。
	 *
	 * @param errorCode 实现了 IErrorCode 接口的错误码枚举或实例
	 * @param cause 原始的、底层的异常对象
	 */
	public BaseException(IErrorCode errorCode, Throwable cause) {
		super(errorCode.getMessage(), cause);
		this.errorCode = errorCode;
	}
}