package com.xksms.common.core;

/**
 * 平台级错误码定义接口。
 * <p>
 * 在 xksms-cloud 体系中，任何一个需要对外暴露的错误码，都必须实现此接口。
 * 它的核心职责是提供一个唯一的、数字类型的错误码和一个对人类可读的错误描述。
 */
public interface IErrorCode {

	/**
	 * 获取错误码。
	 * <p>
	 * 这是一个数字，是系统间或系统与前端进行错误类型识别的唯一依据。
	 *
	 * @return 错误码 (int)
	 */
	int getCode();

	/**
	 * 获取错误描述。
	 * <p>
	 * 这是一段对人类友好的文本，用于在日志、监控或API响应中展示。
	 *
	 * @return 错误描述 (String)
	 */
	String getMessage();
}