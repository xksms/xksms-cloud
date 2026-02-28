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

/**
 * Feign 错误解码器
 * 负责将远程调用的异常响应转换为本地异常对象
 * 支持业务异常、超时、网络错误等多种场景
 */
@Slf4j
public record FeignErrorDecoder(ObjectMapper objectMapper) implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        // 根据 HTTP 状态码判断错误类型
        int status = response.status();

        // 1. 超时场景 (504 Gateway Timeout 或 408 Request Timeout)
        if (status == 504 || status == 408) {
            String timeoutMsg = String.format("远程调用 [%s] 超时，状态码：%d", methodKey, status);
            log.warn(timeoutMsg);
            return new BaseException(new IErrorCode() {
                @Override
                public int getCode() {
                    return 504;
                }

                @Override
                public String getMessage() {
                    return timeoutMsg;
                }
            });
        }

        // 2. 服务不可用场景 (503 Service Unavailable)
        if (status == 503) {
            String unavailableMsg = String.format("远程服务 [%s] 不可用，状态码：%d", methodKey, status);
            log.warn(unavailableMsg);
            return new BaseException(new IErrorCode() {
                @Override
                public int getCode() {
                    return 503;
                }

                @Override
                public String getMessage() {
                    return unavailableMsg;
                }
            });
        }

        // 3. 客户端错误 (4xx) - 通常是参数错误或鉴权失败
        if (status >= 400 && status < 500) {
            String clientErrorMsg = String.format("远程调用 [%s] 客户端错误，状态码：%d", methodKey, status);
            // 尝试解析响应体获取详细错误信息
            String body = parseBody(response);
            if (body != null && !body.isEmpty()) {
                try {
                    Result<?> result = objectMapper.readValue(body, Result.class);
                    if (result.code() != 0 && result.message() != null) {
                        final String finalMsg = String.format("远程调用 [%s] 失败，Code: %d, Message: %s",
                                methodKey, result.code(), result.message());
                        return new BaseException(new IErrorCode() {
                            @Override
                            public int getCode() {
                                return result.code();
                            }

                            @Override
                            public String getMessage() {
                                return finalMsg;
                            }
                        });
                    }
                } catch (IOException e) {
                    // 解析失败，使用默认错误信息
                    log.debug("解析错误响应体失败：{}", body);
                }
            }
            final String finalClientErrorMsg = clientErrorMsg;
            log.warn(clientErrorMsg);
            return new BaseException(new IErrorCode() {
                @Override
                public int getCode() {
                    return status;
                }

                @Override
                public String getMessage() {
                    return finalClientErrorMsg;
                }
            });
        }

        // 4. 服务端错误 (5xx) - 尝试解析业务异常
        if (status >= 500) {
            String body = parseBody(response);
            if (body != null && !body.isEmpty()) {
                try {
                    Result<?> result = objectMapper.readValue(body, Result.class);
                    if (result.code() != 0 && result.message() != null) {
                        IErrorCode remoteError = new IErrorCode() {
                            @Override
                            public int getCode() {
                                return result.code();
                            }

                            @Override
                            public String getMessage() {
                                return result.message();
                            }
                        };
                        log.warn("远程调用 [{}] 服务端错误，Code: {}, Message: {}", methodKey, remoteError.getCode(), remoteError.getMessage());
                        return new BaseException(remoteError);
                    }
                } catch (IOException e) {
                    // 解析失败，使用默认错误信息
                    log.debug("解析错误响应体失败：{}", body);
                }
            }
        }

        // 5. 默认处理 - delegating to default decoder
        return new Default().decode(methodKey, response);
    }

    /**
     * 解析响应体
     * @param response Feign 响应对象
     * @return 响应体字符串，解析失败返回 null
     */
    private String parseBody(Response response) {
        try {
            if (response.body() == null) {
                return null;
            }
            return new String(response.body().asInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("解析响应体失败", e);
            return null;
        }
    }
}
