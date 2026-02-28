package com.xksms.web.config;


import com.xksms.web.handler.GlobalExceptionHandler;
import com.xksms.web.handler.GlobalResponseAdvice;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/**
 * Web 自动配置类
 * 提供全局异常处理、统一响应封装等 Web 治理能力
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class XksmsWebAutoConfiguration {


	@Bean
	@ConditionalOnMissingBean
	public GlobalExceptionHandler globalExceptionHandler() {
		return new GlobalExceptionHandler();
	}

	@Bean
	@ConditionalOnMissingBean
	public GlobalResponseAdvice globalResponseAdvice() {
		return new GlobalResponseAdvice();
	}
}
