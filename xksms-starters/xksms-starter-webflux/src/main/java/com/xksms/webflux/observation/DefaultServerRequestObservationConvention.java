// 位置: xksms-starter-webflux/src/main/java/com/xksms/webflux/observation/DefaultServerRequestObservationConvention.java
package com.xksms.webflux.observation;

import io.micrometer.common.KeyValues;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationConvention;
import lombok.NonNull;

import java.util.Objects;

/**
 * 默认的、用于服务器端HTTP请求的观测约定。
 * 它定义了我们将如何从请求上下文中提取关键信息，并将其转化为标准化的标签（KeyValues），
 * 以便 Metrics 和 Tracing 系统能够统一消费。
 */
public class DefaultServerRequestObservationConvention implements ObservationConvention<ServerRequestObservationContext> {

	private static final String OBSERVATION_NAME = "xksms.http.server.requests";

	@Override
	public boolean supportsContext(@NonNull Observation.Context context) {
		return context instanceof ServerRequestObservationContext;
	}

	@Override
	@NonNull
	public KeyValues getLowCardinalityKeyValues(ServerRequestObservationContext context) {
		// 低基数键值对，用于聚合指标（Metrics）
		return KeyValues.of(
				"http.method", context.getRequest().getMethod().name(),
				"http.status_code", String.valueOf(context.getStatusCode())
		);
	}

	@Override
	@NonNull
	public KeyValues getHighCardinalityKeyValues(ServerRequestObservationContext context) {
		// 高基数键值对，用于链路追踪（Tracing）的详细信息
		return KeyValues.of(
				"http.uri", context.getRequest().getURI().getPath(),
				"client.ip", Objects.requireNonNull(context.getRequest().getRemoteAddress()).getAddress().getHostAddress()
		);
	}

	@Override
	public String getName() {
		return OBSERVATION_NAME;
	}
}