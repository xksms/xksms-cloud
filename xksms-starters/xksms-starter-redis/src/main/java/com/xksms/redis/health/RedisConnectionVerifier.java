// 在 starter 中新建一个类 RedisConnectionVerifier.java

package com.xksms.redis.health;

import com.xksms.redis.properties.XksmsRedisProperties;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;

public record RedisConnectionVerifier(RedisConnectionFactory redisConnectionFactory, XksmsRedisProperties properties) implements ApplicationListener<ApplicationReadyEvent> {

	private static final Logger log = LoggerFactory.getLogger(RedisConnectionVerifier.class);

	@Override
	public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
		try {
			// 执行一个 PING 命令，这是最轻量级的连接检查
			String result = redisConnectionFactory.getConnection().ping();
			if ("PONG".equalsIgnoreCase(result)) {
				log.info("[xksms-starter-redis] Redis 连接校验成功! PING -> PONG ({}:{}/{})",
						properties.getHost(), properties.getPort(), properties.getDatabase());
			} else {
				log.warn("[xksms-starter-redis] Redis 连接校验异常，响应: {}", result);
			}
		} catch (Exception e) {
			log.error("[xksms-starter-redis] Redis 连接校验失败! 无法连接到 {}:{}/{}. 请检查配置或网络状态。",
					properties.getHost(), properties.getPort(), properties.getDatabase(), e);
		}
	}
}