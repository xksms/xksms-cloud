package com.xksms.webflux.config;// 位置: xksms-starter-webflux/src/main/java/com/xksms/webflux/config/XksmsWebfluxAutoConfiguration.java
// ...

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xksms.webflux.handler.GlobalReactiveExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class XksmsWebfluxAutoConfiguration {
	@Bean
	@Order(-2)
	public GlobalReactiveExceptionHandler globalReactiveExceptionHandler(ObjectMapper objectMapper) {
		return new GlobalReactiveExceptionHandler(objectMapper);
	}
}