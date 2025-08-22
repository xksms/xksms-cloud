package com.xksms.redis.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * XK-SMS Redis 客户端统一配置属性。
 * <p>
 * 该类将所有 Redis 相关的配置收敛到 "xksms.redis" 命名空间下，
 * 为平台提供统一、清晰的 Redis 配置入口。
 * Starter 内部会自动将这些配置映射到 Spring Boot 底层的 Redis 连接池等组件。
 */
@Data
@ConfigurationProperties(prefix = "xksms.redis")
public class XksmsRedisProperties {

	/**
	 * 是否启用 XK-SMS Redis Starter 的自动配置。
	 */
	private boolean enabled = true;

	/**
	 * Redis 服务器地址。
	 */
	private String host = "localhost";

	/**
	 * Redis 服务器端口。
	 */
	private int port = 6379;

	/**
	 * Redis 数据库索引（0-15）。
	 */
	private int database = 0;

	/**
	 * Redis 服务器连接密码。
	 */
	private String password;

	/**
	 * 连接超时时间（毫秒）。
	 */
	private int timeout = 5000;

	/**
	 * 是否在应用启动完成后执行一次连接校验。
	 * 建议在开发和测试环境开启，生产环境可以关闭以加快启动速度。
	 */
	private boolean verifyOnStartup = true;

	/**
	 * Lettuce 客户端配置。
	 */
	private Lettuce lettuce = new Lettuce();

	@Data
	public static class Lettuce {
		/**
		 * Lettuce 连接池配置。
		 */
		private Pool pool = new Pool();
	}

	@Data
	public static class Pool {
		/**
		 * 连接池中的最大连接数。
		 */
		private int maxActive = 8;
		/**
		 * 连接池中的最大空闲连接。
		 */
		private int maxIdle = 8;
		/**
		 * 连接池中的最小空闲连接。
		 */
		private int minIdle = 0;
		/**
		 * 当连接池耗尽时，获取连接所等待的最大时间（毫秒）。
		 */
		private int maxWait = -1;
	}
}