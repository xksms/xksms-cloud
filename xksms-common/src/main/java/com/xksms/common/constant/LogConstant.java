package com.xksms.common.constant;

/**
 * 日志与链路追踪相关的公共常量。
 * <p>
 * 设计为 final class 并提供私有构造函数，以防止任何形式的实例化和继承。
 * 这是一个标准的工具类（Utility Class）设计模式。
 */
public final class LogConstant {

	/**
	 * 私有构造函数，防止该类被实例化。
	 */
	private LogConstant() {
		// 防止通过反射进行实例化
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	/**
	 * W3C Trace Context 标准请求头（https://www.w3.org/TR/trace-context/）。
	 * 这是我们 xksms-cloud 体系对外交互时，首选的、用于传播分布式链路上下文的 Header。
	 * 它具备全球通用性，能够与各种现代可观测性系统（如 OpenTelemetry）无缝集成。
	 * 它的值由 traceId, spanId 等多部分组成。
	 */
	public static final String TRACE_PARENT_HEADER = "traceparent";

	/**
	 * 一个简化的、自定义的请求 ID Header。
	 * 当与不支持 W3C Trace Context 的老旧或简单系统交互时，可使用此 Header 来传递纯粹的 traceId 值。
	 * 它作为 traceparent 的一个兼容性补充而存在。
	 */
	public static final String REQUEST_ID_HEADER = "X-Request-ID";

	/**
	 * 在 ServerWebExchange/HttpServletRequest 的 attributes 中，用于存放我们系统内部规范化后的 Trace ID 的 KEY。
	 * 采用点分命名空间（xksms.）是为了防止与 Spring 框架或第三方库可能设置的属性产生命名冲突。
	 * 这是我们在进程内（Inter-Process）传递和获取 Trace ID 的权威“内部信源”。
	 */
	public static final String XKSMS_TRACE_ID_ATTRIBUTE = "xksms.trace.id";


	/**
	 * SkyWalking V8 标准请求头。
	 * 我们定义此常量，主要用于在网关入口处【解析】从其他已接入SkyWalking的系统传来的链路信息。
	 * 在我们的体系中，我们【不主动生成】此 Header，而是统一生成更具通用性的 traceparent Header。
	 */
	public static final String SW8_HEADER = "sw8";

}