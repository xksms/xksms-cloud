package com.xksms.elk.config;

import ch.qos.logback.classic.Logger;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;


@AutoConfiguration
@ConditionalOnClass(Logger.class)
public class LogAutoConfiguration {

	@Bean
	public LogbackConfiguration logbackConfiguration() {
		return new LogbackConfiguration();
	}
}
