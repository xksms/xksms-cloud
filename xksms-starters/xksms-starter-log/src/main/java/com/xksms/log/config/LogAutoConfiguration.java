package com.xksms.log.config;

import com.xksms.log.filter.TraceIdMdcFilter;
import com.xksms.log.properties.LogstashProperties;
import com.xksms.log.spi.DefaultLogUserProvider;
import com.xksms.log.spi.LogUserProvider;
import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(LogstashProperties.class)
public class LogAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public FilterRegistrationBean<Filter> traceIdMdcFilter(LogUserProvider logUserProvider) {
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new TraceIdMdcFilter(logUserProvider)); // 构造注入
		registration.setOrder(-999);
		registration.addUrlPatterns("/*");
		return registration;
	}

	@Bean
	@ConditionalOnMissingBean(LogUserProvider.class)
	public LogUserProvider logUserProvider() {
		return new DefaultLogUserProvider();
	}

	@Bean
	public LogbackConfiguration logbackConfiguration() {
		return new LogbackConfiguration();
	}

}
