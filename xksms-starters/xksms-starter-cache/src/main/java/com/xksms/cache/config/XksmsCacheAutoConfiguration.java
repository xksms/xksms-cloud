package com.xksms.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.xksms.cache.properties.XksmsCacheProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.Map;

// [最终架构决策版]
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(XksmsCacheProperties.class)
@ConditionalOnProperty(prefix = "xksms.cache", name = "enabled", havingValue = "true", matchIfMissing = true)
// 当这个自动配置类生效时，它就等同于在应用的启动类上添加了 @EnableCaching。
@EnableCaching
public class XksmsCacheAutoConfiguration {

	// ... cacheManager Bean 和 switch 表达式保持不变 ...
	@Bean
	@Primary
	public CacheManager cacheManager(
			XksmsCacheProperties properties,
			RedisConnectionFactory redisConnectionFactory,
			RedisSerializer<Object> redisValueSerializer
	) {
		return switch (properties) {
			case XksmsCacheProperties props when props.l1Enabled() && props.l2Enabled() -> {
				log.info("[xksms-starter-cache] 启用 L1 (Caffeine) + L2 (Redis) 两级缓存模式。");
				// 注意：props.ttl() 只传递给了 buildRedisCacheManager
				yield new CompositeCacheManager(
						buildCaffeineCacheManager(props.caffeine()),
						buildRedisCacheManager(redisConnectionFactory, redisValueSerializer, props.ttl())
				);
			}
			case XksmsCacheProperties props when props.l1Enabled() -> {
				log.info("[xksms-starter-cache] 仅启用 L1 (Caffeine) 本地缓存模式。");
				yield buildCaffeineCacheManager(props.caffeine());
			}
			case XksmsCacheProperties props when props.l2Enabled() -> {
				log.info("[xksms-starter-cache] 仅启用 L2 (Redis) 分布式缓存模式。");
				yield buildRedisCacheManager(redisConnectionFactory, redisValueSerializer, props.ttl());
			}
			default -> {
				log.warn("[xksms-starter-cache] L1 和 L2 缓存均未启用，将使用一个空操作的 CacheManager。");
				yield new CompositeCacheManager();
			}
		};
	}

	/**
	 * [核心重构] 辅助方法：构建 CaffeineCacheManager。
	 * 架构决策：L1 缓存使用全局统一的配置，不支持按 cache name 设置不同 TTL。
	 * 因此，此方法不再接收 ttlMap 参数。
	 */
	private CaffeineCacheManager buildCaffeineCacheManager(XksmsCacheProperties.Caffeine caffeineProps) {
		CaffeineCacheManager manager = new CaffeineCacheManager();
		Caffeine<Object, Object> builder = Caffeine.newBuilder()
				.initialCapacity(caffeineProps.initialCapacity())
				.maximumSize(caffeineProps.maximumSize());

		// 我们可以在这里为所有 L1 缓存设置一个全局的、统一的、较短的过期时间
		// builder.expireAfterWrite(Duration.ofMinutes(1)); // 例如，所有 L1 缓存都 1 分钟过期

		manager.setCaffeine(builder);
		return manager;
	}

	/**
	 * 辅助方法：构建 RedisCacheManager。
	 * 它会正确地使用 ttlMap 来为 L2 缓存设置专属 TTL。
	 */
	private RedisCacheManager buildRedisCacheManager(
			RedisConnectionFactory factory,
			RedisSerializer<Object> valueSerializer,
			Map<String, Duration> ttlMap
	) {
		RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer));

		Map<String, RedisCacheConfiguration> initialCacheConfigs = new java.util.HashMap<>();
		if (ttlMap != null) {
			ttlMap.forEach((name, ttl) -> initialCacheConfigs.put(name, defaultConfig.entryTtl(ttl)));
		}

		return RedisCacheManager.builder(factory)
				.cacheDefaults(defaultConfig)
				.withInitialCacheConfigurations(initialCacheConfigs)
				.build();
	}
}