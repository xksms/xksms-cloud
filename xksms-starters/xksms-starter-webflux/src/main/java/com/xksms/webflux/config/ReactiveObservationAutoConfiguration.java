package com.xksms.webflux.config;// 位置: xksms-starter-webflux/src/main/java/com/xksms/webflux/config/ReactiveObservationAutoConfiguration.java
// ...

import com.xksms.webflux.filter.ObservedWebfluxFilter;
import com.xksms.webflux.observation.LoggingObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
//确保 Tracer Bean 存在时，才启用我们的日志 Handler
@ConditionalOnBean({ObservationRegistry.class, Tracer.class})
public class ReactiveObservationAutoConfiguration {

	@Bean
	public ObservedWebfluxFilter observedWebfluxFilter(ObservationRegistry observationRegistry) {
		return new ObservedWebfluxFilter(observationRegistry);
	}

	/**
	 * 【新增】将我们的日志处理器注册为一个 Bean。
	 * Spring Boot 的 ObservationAutoConfiguration 会自动发现所有 ObservationHandler 类型的 Bean，
	 * 并将它们添加到全局的 ObservationRegistry 中。
	 */
	@Bean
	public LoggingObservationHandler loggingObservationHandler(Tracer tracer) {
		return new LoggingObservationHandler(tracer);
	}
}