package com.xksms.rpc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xksms.rpc.decoder.FeignErrorDecoder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class XksmsRpcAutoConfiguration {

	@Bean
	public FeignErrorDecoder feignErrorDecoder(ObjectMapper objectMapper) {
		return new FeignErrorDecoder(objectMapper);
	}
}