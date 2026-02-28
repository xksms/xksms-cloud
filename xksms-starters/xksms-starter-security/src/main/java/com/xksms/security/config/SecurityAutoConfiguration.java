package com.xksms.security.config;

import com.xksms.log.spi.LogUserProvider;
import com.xksms.security.jwt.JwtAuthenticationFilter;
import com.xksms.security.jwt.JwtTokenParser;
import com.xksms.security.spi.SecurityLogUserProvider;
import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;


/**
 * 安全认证自动配置类
 * 提供 JWT 认证过滤器、Token 解析器、日志用户提供者等核心组件的自动装配
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass({jakarta.servlet.Filter.class, org.springframework.security.web.SecurityFilterChain.class})
public class SecurityAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public JwtTokenParser jwtTokenParser() {
		return new JwtTokenParser();
	}

	@Bean
	@ConditionalOnMissingBean(name = "jwtAuthenticationFilter")
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
