package com.xksms.rpc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xksms.rpc.decoder.FeignErrorDecoder;
import feign.codec.ErrorDecoder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * RPC 自动配置类
 * 提供 OpenFeign 的错误解码器、日志拦截器等核心组件
 */
@AutoConfiguration
@ConditionalOnClass({ErrorDecoder.class})
public class XksmsRpcAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public FeignErrorDecoder feignErrorDecoder(ObjectMapper objectMapper) {
		return new FeignErrorDecoder(objectMapper);
	}
}