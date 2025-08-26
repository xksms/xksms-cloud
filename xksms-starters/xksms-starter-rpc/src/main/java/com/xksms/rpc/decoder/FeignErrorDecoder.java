package com.xksms.rpc.decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xksms.common.core.BaseException;
import com.xksms.common.core.IErrorCode;
import com.xksms.common.core.Result;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public record FeignErrorDecoder(ObjectMapper objectMapper) implements ErrorDecoder {

	@Override
	public Exception decode(String methodKey, Response response) {
		try {
			String body = new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);

			// 只有当响应体不为空时才尝试解析
			if (!body.isEmpty()) {
				Result<?> result = objectMapper.readValue(body, Result.class);
				if (result.getCode() != 0 && result.getMessage() != null) {
					IErrorCode remoteError = new IErrorCode() {
						@Override
						public int getCode() {
							return result.getCode();
						}

						@Override
						public String getMessage() {
							return result.getMessage();
						}
					};
					log.warn("远程调用 [{}] 失败, Code: {}, Message: {}", methodKey, remoteError.getCode(), remoteError.getMessage());
					return new BaseException(remoteError);
				}
			}
		} catch (IOException e) {
			log.error("Feign ErrorDecoder 解析远程异常响应失败, methodKey: {}", methodKey, e);
		}

		return new Default().decode(methodKey, response);
	}
}