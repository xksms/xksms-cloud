package com.xksms.security.config;

import com.xksms.log.spi.LogUserProvider;
import com.xksms.security.jwt.JwtAuthenticationFilter;
import com.xksms.security.jwt.JwtTokenParser;
import com.xksms.security.spi.SecurityLogUserProvider;
import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;


@AutoConfiguration
public class SecurityAutoConfiguration {

	@Bean
	public JwtTokenParser jwtTokenParser() {
		return new JwtTokenParser();
	}

	@Bean
	public FilterRegistrationBean<Filter> jwtAuthenticationFilter(JwtTokenParser parser) {
		FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
		registration.setFilter(new JwtAuthenticationFilter(parser));
		registration.setOrder(-990);
		registration.addUrlPatterns("/*");
		return registration;
	}

	@Bean
	@ConditionalOnMissingBean(LogUserProvider.class)
	public LogUserProvider logUserProvider() {
		return new SecurityLogUserProvider();
	}
}
