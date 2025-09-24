package com.xksms.log.config;

import com.xksms.log.properties.LogstashProperties;
import com.xksms.log.spi.DefaultLogUserProvider;
import com.xksms.log.spi.LogUserProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(LogstashProperties.class)
public class XksmsLogAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(LogUserProvider.class)
	public LogUserProvider logUserProvider() {
		return new DefaultLogUserProvider();
	}


	@Bean
	public XksmsLogbackConfiguration logbackConfiguration() {
		return new XksmsLogbackConfiguration();
	}


}
