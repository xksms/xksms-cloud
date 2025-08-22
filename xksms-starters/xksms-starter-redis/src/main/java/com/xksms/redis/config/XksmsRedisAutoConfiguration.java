package com.xksms.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.xksms.redis.health.RedisConnectionVerifier;
import com.xksms.redis.properties.XksmsRedisProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.NonNull;

// [最终方案]
@AutoConfiguration
@EnableConfigurationProperties(XksmsRedisProperties.class)
@ConditionalOnProperty(prefix = "xksms.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
public class XksmsRedisAutoConfiguration {

	private static final Logger log = LoggerFactory.getLogger(XksmsRedisAutoConfiguration.class);

	/**
	 * Bean 后置处理器，用于将我们自定义的 xksms.redis.* 配置“桥接”到 Spring Boot 原生的 RedisProperties。
	 * 这是实现“轻量级”定制的核心。
	 */
	@Bean
	public static BeanPostProcessor xksmsRedisPropertiesProcessor(XksmsRedisProperties xksmsRedisProperties) {
		log.info("[xksms-starter-redis] 正在应用自定义 Redis 配置...");
		return new BeanPostProcessor() {
			@Override
			public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
				// 我们只关心 Spring Boot 官方的 RedisProperties 这个 Bean
				if (bean instanceof RedisProperties nativeProperties) {
					// 从 xksms 配置中读取值
					nativeProperties.setHost(xksmsRedisProperties.getHost());
					nativeProperties.setPort(xksmsRedisProperties.getPort());
					nativeProperties.setPassword(xksmsRedisProperties.getPassword());
					nativeProperties.setDatabase(xksmsRedisProperties.getDatabase());
					nativeProperties.setTimeout(java.time.Duration.ofMillis(xksmsRedisProperties.getTimeout()));

					// 配置连接池
					RedisProperties.Lettuce lettuce = nativeProperties.getLettuce();
					if (lettuce != null) {
						XksmsRedisProperties.Pool poolProps = xksmsRedisProperties.getLettuce().getPool();
						lettuce.getPool().setMaxActive(poolProps.getMaxActive());
						lettuce.getPool().setMaxIdle(poolProps.getMaxIdle());
						lettuce.getPool().setMinIdle(poolProps.getMinIdle());
						lettuce.getPool().setMaxWait(java.time.Duration.ofMillis(poolProps.getMaxWait()));
					}
					log.info("[xksms-starter-redis] 自定义 Redis 配置已成功应用至 Spring Boot 原生配置。目标地址: {}:{}",
							nativeProperties.getHost(), nativeProperties.getPort());
				}
				return bean;
			}
		};
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
	public RedisTemplate<String, Object> redisTemplate(
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
}