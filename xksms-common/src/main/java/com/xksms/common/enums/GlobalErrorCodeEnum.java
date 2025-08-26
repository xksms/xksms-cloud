package com.xksms.common.enums;

import com.xksms.common.core.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局通用错误码枚举。
 * <p>
 * 这个枚举定义了整个 xksms-cloud 平台所有微服务都可以共用的、
 * 与具体业务无关的通用错误类型。
 * 错误码的范围规划：
 * - 负数: 保留给框架或中间件级别的错误。
 * - 1-999: 全局通用错误。
 * - 1000-1999: 用户服务 (user-service) 错误。
 * - 2000-2999: 订单服务 (order-service) 错误。
 * - ... 以此类推
 */
@Getter
@AllArgsConstructor
public enum GlobalErrorCodeEnum implements IErrorCode {

	// ========== 通用 ==========
	SUCCESS(200, "操作成功"),
	SYSTEM_ERROR(500, "系统异常，请联系管理员"),
	SERVICE_UNAVAILABLE(-2, "服务暂不可用，请稍后重试"),

	// ========== 客户端错误段 (1xx) ==========
	BAD_REQUEST(100, "无效的请求"),
	PARAM_VALIDATION_ERROR(101, "参数校验失败"),
	INVALID_TOKEN(102, "无效的令牌或令牌已过期"),
	ACCESS_DENIED(103, "权限不足，禁止访问"),
	RESOURCE_NOT_FOUND(104, "请求的资源不存在"),
	REQUEST_METHOD_NOT_SUPPORTED(105, "不支持的请求方法"),
	DUPLICATE_REQUEST(106, "重复的请求，请勿重复提交");


	private final int code;
	private final String message;
}