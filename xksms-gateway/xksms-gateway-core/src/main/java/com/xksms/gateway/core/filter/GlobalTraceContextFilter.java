package com.xksms.gateway.core.filter;

import com.xksms.common.constant.LogConstant;
import com.xksms.gateway.core.util.TraceHeaderUtil;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Signal;
import reactor.util.context.ContextView;

import java.util.Objects;
import java.util.function.Consumer;

@Component
public class GlobalTraceContextFilter implements GlobalFilter, Ordered {

	// 定义我们要在 Reactor Context 和 MDC 中使用的 KEY
	private static final String TRACE_ID_KEY = "tid";
	private static final String IP_KEY = "ip";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// 1. 在链路起点，捕获所有需要的上下文信息
		// 此时 TraceContext.traceId() 已可用，我们优先使用它，以保证与Agent的上下文一致
		String traceId = TraceContext.traceId();
		// 如果 Agent 因某种原因在 Filter 执行时还未准备好，我们依然从 Header 解析作为备用
		String ipAddress = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress();
		String spanId = TraceHeaderUtil.generateSpanId();
		String traceParentHeader = TraceHeaderUtil.buildTraceParentHeader(traceId, spanId);
		ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
				.header(LogConstant.TRACE_PARENT_HEADER, traceParentHeader)
				.header(LogConstant.REQUEST_ID_HEADER, traceId)
				.build();
		ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();

		return chain.filter(modifiedExchange)
				.contextWrite(ctx -> ctx.put(TRACE_ID_KEY, traceId).put(IP_KEY, ipAddress))
				// 【优化】doOnEach 现在能处理所有情况
				.doOnEach(logOnEach())
				// 【优化】空的 doOnError 已不再需要，可以移除
				.doFinally(signalType -> MDC.clear());
	}

	/**
	 * 辅助方法，用于创建 Signal 消费者，将 Reactor Context 的值桥接到 MDC。
	 * 【已优化】现在可以同时处理成功（onNext）和失败（onError）信号。
	 */
	private <T> Consumer<Signal<T>> logOnEach() {
		return signal -> {
			// 【关键修正】当信号是 onNext 或 onError 时，都进行 MDC 注入
			if (signal.isOnNext() || signal.isOnError()) {
				ContextView context = signal.getContextView();
				if (!context.isEmpty()) {
					context.<String>getOrEmpty(TRACE_ID_KEY).ifPresent(tid -> MDC.put(TRACE_ID_KEY, tid));
					context.<String>getOrEmpty(IP_KEY).ifPresent(ip -> MDC.put(IP_KEY, ip));
				}
			}
		};
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}