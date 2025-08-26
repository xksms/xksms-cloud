package com.xksms.web.handler;

import com.xksms.common.core.BaseException;
import com.xksms.common.core.Result;
import com.xksms.common.enums.GlobalErrorCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BaseException.class)
	// 这明确地告诉调用方（包括 Feign），这是一个客户端类型的错误。
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Result<Void> handleBaseException(BaseException ex) {
		log.warn("业务异常: {}, code: {}", ex.getMessage(), ex.getErrorCode().getCode());
		return Result.failure(ex.getErrorCode());
	}

	/**
	 * 捕获参数校验异常 (@Valid)
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		// 从异常中提取第一个校验失败的错误信息
		String message = ex.getBindingResult().getFieldError().getDefaultMessage();
		log.warn("参数校验失败: {}", message);
		return Result.failure(GlobalErrorCodeEnum.PARAM_VALIDATION_ERROR, message);
	}

	/**
	 * 捕获所有未被处理的异常，作为最终防线
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public Result<Void> handleAllUncaughtException(Exception ex) {
		log.error("系统发生未捕获的异常", ex);
		return Result.failure(GlobalErrorCodeEnum.SYSTEM_ERROR);
	}
}