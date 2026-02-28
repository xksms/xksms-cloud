package com.xksms.log.config;

import com.xksms.log.properties.LogstashProperties;
import com.xksms.log.spi.DefaultLogUserProvider;
import com.xksms.log.spi.LogUserProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 日志自动配置类
 * 提供统一日志规范、TraceId 注入、MDC 配置、LogUserProvider 等能力
 */
@AutoConfiguration
@EnableConfigurationProperties(LogstashProperties.class)
@ConditionalOnProperty(prefix = "xksms.log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class XksmsLogAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(LogUserProvider.class)
	public LogUserProvider logUserProvider() {
		return new DefaultLogUserProvider();
	}


	@Bean
	@ConditionalOnMissingBean
	public XksmsLogbackConfiguration logbackConfiguration() {
		return new XksmsLogbackConfiguration();
	}


}
