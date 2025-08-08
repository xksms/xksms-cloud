package com.xksms.observability.handler;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.tracing.TraceContext;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

/**
 * 一个自定义的 ObservationHandler，核心职责是在观测（Observation）开始时，
 * 将链路追踪的 traceId 和 spanId 注入到 SLF4J 的 MDC (Mapped Diagnostic Context) 中，
 * 并在观测结束时将其清理。
 * <p>
 * 这使得我们的日志（如 logback）可以通过 %X{traceId} 的方式，自动打印出当前请求的链路ID。
 */
public class MdcInjectingObservationHandler implements ObservationHandler<Observation.Context> {

	private static final String TRACE_ID_KEY = "traceId";
	private static final String SPAN_ID_KEY = "spanId";

	@Override
	public void onStart(Observation.Context context) {
		// 尝试从上下文中获取 TracingContext
		TraceContext traceContext = context.get(TraceContext.class);
		if (traceContext != null) {
			// 如果存在，将其 traceId 和 spanId 放入 MDC
			if (StringUtils.hasText(traceContext.traceId())) {
				MDC.put(TRACE_ID_KEY, traceContext.traceId());
			}
			if (StringUtils.hasText(traceContext.spanId())) {
				MDC.put(SPAN_ID_KEY, traceContext.spanId());
			}
		}
	}

	@Override
	public void onStop(@NonNull Observation.Context context) {
		// 观测结束时，无论成功还是失败，都从 MDC 中移除相关键
		MDC.remove(TRACE_ID_KEY);
		MDC.remove(SPAN_ID_KEY);
	}

	@Override
	public boolean supportsContext(@NonNull Observation.Context context) {
		// 我们希望这个 Handler 对所有类型的观测都生效
		return true;
	}
}