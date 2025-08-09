package com.xksms.observability.config;

import com.xksms.observability.handler.MdcInjectingObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * 为 MDC 上下文注入提供自动配置。
 */
@AutoConfiguration
@ConditionalOnClass({ObservationRegistry.class, MdcInjectingObservationHandler.class})
public class MdcContextAutoConfiguration {

	@Bean
	public MdcInjectingObservationHandler mdcInjectingObservationHandler() {
		return new MdcInjectingObservationHandler();
	}
}