package com.xksms.notify.biz.controller;

import com.xksms.common.core.BaseException;
import com.xksms.common.enums.GlobalErrorCodeEnum;
import com.xksms.notify.api.dto.NotificationDTO;
import com.xksms.notify.biz.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping(value = "/notifications/stream/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<NotificationDTO> streamNotifications(@PathVariable String userId) {
		return notificationService.createStreamForUser(userId);
	}

	@PostMapping("/notifications/internal/push")
	public Mono<Void> pushNotification(@RequestBody NotificationDTO notification) {
		notification.setEventId(UUID.randomUUID().toString());
		notification.setTimestamp(LocalDateTime.now());

		notificationService.pushNotificationToUser(notification);
		return Mono.empty();
	}

	/**
	 * [测试端点]
	 * 用于专门测试全局响应式异常处理器。
	 * @return 一个会立即发射 BaseException 错误信号的 Mono。
	 */
	@GetMapping("/notifications/test/error")
	public Mono<Void> testGlobalErrorHandler() {
		// [核心] 使用 Mono.error() 在响应式流中主动触发一个异常。
		// 我们传入一个自定义的 BaseException 实例，并使用一个通用的错误码。
		return Mono.error(new BaseException(GlobalErrorCodeEnum.BAD_REQUEST, "这是一个WebFlux的业务异常测试"));
	}
}