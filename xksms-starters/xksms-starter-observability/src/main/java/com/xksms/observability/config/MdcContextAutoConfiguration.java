package com.xksms.observability.config;

import com.xksms.observability.handler.MdcInjectingObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * MDC 上下文自动配置类
 * 为链路追踪提供 MDC 上下文注入能力
 */
@AutoConfiguration
@ConditionalOnClass({ObservationRegistry.class, MdcInjectingObservationHandler.class})
public class MdcContextAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public MdcInjectingObservationHandler mdcInjectingObservationHandler() {
		return new MdcInjectingObservationHandler();
	}
}