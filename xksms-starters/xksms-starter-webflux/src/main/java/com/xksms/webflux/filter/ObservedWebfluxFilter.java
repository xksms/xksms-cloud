// 位置: xksms-starter-webflux/src/main/java/com/xksms/webflux/filter/ObservedWebfluxFilter.java
package com.xksms.webflux.filter;

import com.xksms.webflux.observation.DefaultServerRequestObservationConvention;
import com.xksms.webflux.observation.ServerRequestObservationContext;
import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;

@RequiredArgsConstructor
public class ObservedWebfluxFilter implements WebFilter, Ordered {

	private final ObservationRegistry registry;
	private final DefaultServerRequestObservationConvention convention = new DefaultServerRequestObservationConvention();

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerRequestObservationContext context = new ServerRequestObservationContext(exchange.getRequest());

		return Mono.just(context)
				.flatMap(ctx -> Observation.createNotStarted(convention, () -> ctx, registry)
						.observe(() -> chain.filter(exchange)))
				.doOnSuccess(aVoid -> recordStatusCode(exchange, context))
				.doOnError(throwable -> recordStatusCode(exchange, context));
	}

	private void recordStatusCode(ServerWebExchange exchange, ServerRequestObservationContext context) {
		Integer statusCode = Optional.ofNullable(exchange.getResponse().getStatusCode())
				.map(HttpStatusCode::value)
				.orElse(500);
		context.setStatusCode(statusCode);
	}

	@Override
	public int getOrder() {
		// 确保这是最先执行的过滤器之一，以便包裹住所有后续操作
		return Ordered.HIGHEST_PRECEDENCE;
	}
}