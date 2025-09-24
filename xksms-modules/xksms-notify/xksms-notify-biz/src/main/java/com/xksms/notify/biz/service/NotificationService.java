package com.xksms.notify.biz.service;


import com.xksms.notify.api.dto.NotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class NotificationService {

	private final Map<String, Sinks.Many<NotificationDTO>> userSinks = new ConcurrentHashMap<>();

	public Flux<NotificationDTO> createStreamForUser(String userId) {
		Sinks.Many<NotificationDTO> sink = userSinks.computeIfAbsent(userId, id -> {
			log.info("为用户 [{}] 创建新的通知流 (Sink)", id);
			return Sinks.many().multicast().onBackpressureBuffer();
		});

		// [核心] 返回 Sink 对应的 Flux，并附加清理逻辑
		return sink.asFlux()
				.doOnCancel(() -> { // 当客户端取消订阅时 (例如关闭浏览器)
					log.info("用户 [{}] 的通知流被取消，准备清理资源", userId);
					userSinks.remove(userId);
				})
				.doOnTerminate(() -> { // 当流正常或异常终止时
					log.info("用户 [{}] 的通知流已终止，准备清理资源", userId);
					userSinks.remove(userId);
				});
	}

	public void pushNotificationToUser(NotificationDTO notification) {
		String userId = notification.getRecipientId();
		Sinks.Many<NotificationDTO> sink = userSinks.get(userId);

		if (sink != null) {
			log.info("向用户 [{}] 推送通知: {}", userId, notification.getContent());
			Sinks.EmitResult result = sink.tryEmitNext(notification);
			if (result.isFailure()) {
				log.warn("向用户 [{}] 推送通知失败, EmitResult: {}, 可能是下游已断开", userId, result);
				// 失败时也尝试清理，因为很可能是连接已经失效
				userSinks.remove(userId);
			}
		} else {
			log.warn("用户 [{}] 不在线，推送被忽略: {}", userId, notification.getContent());
		}
	}
}