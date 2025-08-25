package com.xksms.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.xksms.redis.RedisHelper;
import com.xksms.redis.health.RedisConnectionVerifier;
import com.xksms.redis.properties.XksmsRedisProperties;
import io.lettuce.core.api.StatefulConnection;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

// 我们不再使用 BeanPostProcessor，而是自己完整地定义核心 Bean
@AutoConfiguration(before = RedisAutoConfiguration.class)
@EnableConfigurationProperties(XksmsRedisProperties.class)
@ConditionalOnProperty(prefix = "xksms.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
public class XksmsRedisAutoConfiguration {

	private static final Logger log = LoggerFactory.getLogger(XksmsRedisAutoConfiguration.class);

	/**
	 * [核心] 我们亲自创建并注册 LettuceConnectionFactory 这个核心 Bean。
	 * 因为 Spring Boot 的 RedisAutoConfiguration 中有 @ConditionalOnMissingBean(RedisConnectionFactory.class)，
	 * 我们的这个 Bean 一旦被注册，Spring Boot 的整个自动配置就会优雅地“让路”，从而从根本上避免了任何冲突。
	 */
	@Bean
	@ConditionalOnMissingBean(RedisConnectionFactory.class)
	public LettuceConnectionFactory redisConnectionFactory(XksmsRedisProperties properties) {
		log.info("[xksms-starter-redis] 正在创建自定义的 LettuceConnectionFactory...");
		RedisStandaloneConfiguration standaloneConfig = new RedisStandaloneConfiguration();
		standaloneConfig.setHostName(properties.getHost());
		standaloneConfig.setPort(properties.getPort());
		standaloneConfig.setDatabase(properties.getDatabase());
		if (properties.getPassword() != null && !properties.getPassword().isEmpty()) {
			standaloneConfig.setPassword(properties.getPassword());
		}

		XksmsRedisProperties.Pool poolProps = properties.getLettuce().getPool();
		GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
		poolConfig.setMaxTotal(poolProps.getMaxActive());
		poolConfig.setMaxIdle(poolProps.getMaxIdle());
		poolConfig.setMinIdle(poolProps.getMinIdle());
		poolConfig.setMaxWait(Duration.ofMillis(poolProps.getMaxWait()));

		LettucePoolingClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
				.commandTimeout(Duration.ofMillis(properties.getTimeout()))
				.poolConfig(poolConfig)
				.build();

		return new LettuceConnectionFactory(standaloneConfig, clientConfig);
	}

	/**
	 * [核心重构] 将 Value 序列化器作为一个独立的 Bean 注册到 Spring 容器。
	 * 这样，任何其他需要统一序列化方案的模块（如 xksms-starter-cache）
	 * 都可以直接注入这个 Bean。
	 *
	 * @return RedisSerializer<Object>
	 */
	@Bean
	@ConditionalOnMissingBean(name = "redisValueSerializer")
	public RedisSerializer<Object> redisValueSerializer() {
		// 1. 创建一个定制化的 ObjectMapper，用于序列化
		ObjectMapper objectMapper = new ObjectMapper();
		// 2. 指定要序列化的域
		objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		// 3. 指定序列化输入的类型
		objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
		return new GenericJackson2JsonRedisSerializer(objectMapper);
	}

	/**
	 * [核心修正] RedisTemplate 的 Bean 定义现在依赖注入上面创建的 redisValueSerializer Bean。
	 * 它不再关心序列化器是如何被创建的，只关心如何使用它。
	 */
	@Bean
	// [核心引导] 我们不再将这个 Bean 命名为 "redisTemplate"。
	// 我们给它一个更底层的、暗示“不建议直接使用”的名字。
	@ConditionalOnMissingBean(name = "xksmsCoreRedisTemplate")
	public RedisTemplate<String, Object> xksmsCoreRedisTemplate(
			RedisConnectionFactory redisConnectionFactory,
			RedisSerializer<Object> redisValueSerializer // <-- 直接注入
	) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);

		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		template.setKeySerializer(stringSerializer);
		template.setHashKeySerializer(stringSerializer);

		// 使用注入的 Value 序列化器
		template.setValueSerializer(redisValueSerializer);
		template.setHashValueSerializer(redisValueSerializer);

		template.afterPropertiesSet();
		return template;
	}

	/**
	 * 为 RedisConnectionVerifier 添加 @Bean 定义，并正确设置 @ConditionalOnProperty。
	 * * `matchIfMissing = true` 是关键：
	 * 它告诉 Spring Boot，即使在 application.yml 中没有找到 "xksms.redis.verify-on-startup" 这个属性，
	 * 也应该默认创建这个 Bean。
	 * * 这样，开发者只有在想显式禁用它时，才需要在配置中加入：
	 * xksms.redis.verify-on-startup: false
	 */
	@Bean
	@ConditionalOnProperty(prefix = "xksms.redis", name = "verify-on-startup", havingValue = "true", matchIfMissing = true)
	public RedisConnectionVerifier redisConnectionVerifier(RedisConnectionFactory factory, XksmsRedisProperties properties) {
		// 使用 Record 来创建实例
		return new RedisConnectionVerifier(factory, properties);
	}

	@Bean
	@ConditionalOnMissingBean(RedisHelper.class)
	public RedisHelper redisHelper(RedisTemplate<String, Object> redisTemplate) {
		log.info("[xksms-starter-redis] 正在创建健壮的 RedisHelper Bean...");
		return new RedisHelper(redisTemplate);
	}
}