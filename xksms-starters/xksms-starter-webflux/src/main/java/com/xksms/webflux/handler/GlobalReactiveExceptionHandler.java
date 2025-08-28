package com.xksms.webflux.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xksms.common.core.BaseException;
import com.xksms.common.core.Result;
import com.xksms.common.enums.GlobalErrorCodeEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public record GlobalReactiveExceptionHandler(ObjectMapper objectMapper) implements ErrorWebExceptionHandler {

	@Override
	@NonNull
	public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
		ServerHttpResponse response = exchange.getResponse();

		// 如果响应已经被处理，则直接返回
		if (response.isCommitted()) {
			return Mono.error(ex);
		}

		// 设置响应头
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		// 根据异常类型决定响应体和HTTP状态码
		Result<?> resultBody;
		if (ex instanceof BaseException baseEx) { // 使用 pattern matching
			response.setStatusCode(HttpStatus.BAD_REQUEST);

			// [核心修正]
			// 我们不再使用 errorCode.getMessage()，而是直接使用异常自身的 getMessage()，
			// 因为它现在可能包含我们自定义的消息。
			resultBody = Result.failure(baseEx.getErrorCode().getCode(), baseEx.getMessage());

		} else {
			// 对于所有其他未知异常，返回统一的系统错误
			log.error("响应式流程中发生未捕获的异常", ex);
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			resultBody = Result.failure(GlobalErrorCodeEnum.SYSTEM_ERROR);
		}

		// 将 Result 对象序列化为 JSON 字节流
		try {
			byte[] bytes = objectMapper.writeValueAsBytes(resultBody);
			DataBuffer buffer = response.bufferFactory().wrap(bytes);
			return response.writeWith(Mono.just(buffer));
		} catch (JsonProcessingException e) {
			log.error("序列化错误响应体失败", e);
			// 如果序列化失败，返回一个最简单的错误响应
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
			return response.setComplete();
		}
	}
}