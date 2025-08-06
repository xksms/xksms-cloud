// 位置: xksms-starter-webflux/src/main/java/com/xksms/webflux/observation/LoggingObservationHandler.java
package com.xksms.webflux.observation;

import io.micrometer.common.KeyValue;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

/**
 * 一个专门用于记录请求日志的 ObservationHandler。
 * 它会“订阅” ServerRequestObservationContext 类型的观测事件，
 * 并在事件的生命周期关键点（开始、结束、出错）打印结构化的日志。
 * 这实现了“观测”与“日志记录”的完美职责分离。
 */
@Slf4j
@RequiredArgsConstructor
public class LoggingObservationHandler implements ObservationHandler<ServerRequestObservationContext> {

	private final Tracer tracer;

	@Override
	public void onStart(ServerRequestObservationContext context) {
		// onStart 的职责变得非常纯粹：只记录开始时间，不做任何日志打印
		context.setStartTimeNanos(System.nanoTime());
	}

	/**
	 * 【核心】当观测的作用域（Scope）被打开后，此方法被调用。
	 * 此时，负责追踪的 Handler 已经将 traceId 等信息放入了 MDC。
	 * 这是打印“请求开始”日志的最佳时机。
	 */
	@Override
	public void onScopeOpened(ServerRequestObservationContext context) {
		String clientIp = findKeyValue(context, "client.ip").orElse("N/A");
		log.info("Request In: {} {}, IP: {}",
				context.getRequest().getMethod(),
				context.getRequest().getURI(),
				clientIp);
	}

	@Override
	public void onStop(ServerRequestObservationContext context) {
		// onStop 在作用域关闭（MDC被清理）之前被调用，所以此时 MDC 依然有效
		long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - context.getStartTimeNanos());
		log.info("Request Out: Status [{}], Cost [{}ms]",
				context.getStatusCode(),
				durationMillis);
	}

	@Override
	public void onError(ServerRequestObservationContext context) {
		// onError 同样在作用域关闭前被调用
		long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - context.getStartTimeNanos());
		log.error("Request Out with Error: Status [{}], Cost [{}ms], Error [{}]",
				context.getStatusCode(),
				durationMillis,
				context.getError().getMessage(),
				context.getError());
	}


	/**
	 * 一个辅助方法，用于从 Observation.Context 中安全地查找指定 Key 的值。
	 * @param context 观测上下文
	 * @param key 要查找的键
	 * @return 包含值的 Optional，如果找不到则为空
	 */
	private Optional<String> findKeyValue(ServerRequestObservationContext context, String key) {
		return StreamSupport.stream(context.getHighCardinalityKeyValues().spliterator(), false)
				.filter(keyValue -> key.equals(keyValue.getKey()))
				.map(KeyValue::getValue)
				.findFirst();
	}

	@Override
	public boolean supportsContext(Observation.Context context) {
		return context instanceof ServerRequestObservationContext;
	}
}