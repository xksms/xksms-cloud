package com.xksms.gateway.core.filter;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * [重构后]
 * 一个纯粹的、基于 Micrometer Observation 的全局链路追踪过滤器。
 *
 * 它的唯一职责是为每个进入网关的请求创建一个 Observation Scope，
 * 将链路上下文的生命周期管理完全委托给 Micrometer 框架。
 *
 * 它不再关心 TraceId 的具体生成、解析或传递方式，实现了与具体追踪系统（如SkyWalking）的解耦。
 * MDC 的注入将由 xksms-starter-observability 中的 MdcInjectingObservationHandler 统一负责。
 */
@Component
public class GlobalTraceContextFilter implements GlobalFilter, Ordered {

	private final ObservationRegistry registry;

	public GlobalTraceContextFilter(ObservationRegistry registry) {
		this.registry = registry;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// 1. 从交换上下文中提取关键信息，用于创建 Observation
		// Spring Cloud Gateway 自动创建的 Observation 默认名为 "http server observation"
		// 我们可以通过 ObservationConvention 来定制其 name 和 tags
		final String path = exchange.getRequest().getPath().value();
		final String method = exchange.getRequest().getMethod().name();

		// 2. 创建并启动 Observation
		// 这是核心：我们只“声明”一个观测点，具体的 TraceId 生成、传播等
		// 都由 Micrometer 的后端（Tracer）自动完成。
		Observation observation = Observation.createNotStarted("gateway.http.requests", this.registry)
				.contextualName("http " + method.toLowerCase() + " " + path)
				.lowCardinalityKeyValue("http.method", method)
				.lowCardinalityKeyValue("http.route", path); // 路由匹配后可以换成更精确的 routeId

		return chain.filter(exchange)
				// 3. 将 Observation 的作用域（Scope）绑定到整个响应式流中
				// ObservationThreadLocalAccessor.KEY 会确保 TraceId 等信息
				// 在 Reactor 的 Context 中自动传递。
				.transform(mono -> mono.contextWrite(context -> context.put(ObservationThreadLocalAccessor.KEY, observation.start())));
	}

	@Override
	public int getOrder() {
		// 确保在所有业务 Filter 之前执行
		return Ordered.HIGHEST_PRECEDENCE;
	}
}