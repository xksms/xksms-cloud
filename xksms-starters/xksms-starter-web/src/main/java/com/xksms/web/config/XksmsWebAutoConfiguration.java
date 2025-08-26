package com.xksms.web.config;


import com.xksms.web.handler.GlobalExceptionHandler;
import com.xksms.web.handler.GlobalResponseAdvice;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class XksmsWebAutoConfiguration {


	@Bean
	public GlobalExceptionHandler globalExceptionHandler() {
		return new GlobalExceptionHandler();
	}

	@Bean
	public GlobalResponseAdvice globalResponseAdvice() {
		return new GlobalResponseAdvice();
	}
}
