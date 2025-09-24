package com.xksms.web.handler;

import com.xksms.common.core.Result;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "com.xksms") // 你可以限定只对特定包生效
public class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		// 如果Controller的返回类型已经是Result，则我们不再处理，直接返回false
		return !returnType.getParameterType().isAssignableFrom(Result.class);
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
								  Class<? extends HttpMessageConverter<?>> selectedConverterType,
								  ServerHttpRequest request, ServerHttpResponse response) {
		// 对于成功的响应，我们将其统一包装在Result.success()中
		if (body == null) {
			return Result.success();
		}
		// 如果body已经是Result类型，则直接返回，避免重复包装
		if (body instanceof Result) {
			return body;
		}
		// 否则，进行包装
		return Result.success(body);
	}
}