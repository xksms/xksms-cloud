package com.xksms.cache.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

/**
 * XK-SMS 统一缓存 Starter 配置属性。
 * <p>
 * 该类定义了 L1(Caffeine) 和 L2(Redis) 两级缓存的开关与核心参数。
 * 我们在这里使用了 Java Record 来定义内嵌的配置块，
 * 这使得代码更简洁，并从语法层面保证了配置对象的不可变性。
 */
@ConfigurationProperties(prefix = "xksms.cache")
public record XksmsCacheProperties(
		/*
		 * 是否启用一级缓存 (Caffeine)。
		 */
		boolean l1Enabled,

		/*
		 * 是否启用二级缓存 (Redis)。
		 */
		boolean l2Enabled,

		/*
		 * [核心] 使用 Record 定义 Caffeine 的专属配置块。
		 * 这使得我们可以在 yml 中使用 xksms.cache.caffeine.initial-capacity 这样的结构。
		 */
		@NestedConfigurationProperty
		Caffeine caffeine,

        /*
          定义不同缓存区域(cache names)的过期时间（TTL）。
          Key: 缓存名，例如 "users", "products"
          Value: 过期时间，例如 "30s", "10m", "1h"
         */
		Map<String, Duration> ttl
) {
	/**
	 * 为 Caffeine 配置块定义一个 Record。
	 * 它是一个不可变的数据载体，包含了 Caffeine 缓存的核心参数。
	 *
	 * @param initialCapacity 初始容量
	 * @param maximumSize 最大容量
	 */
	public record Caffeine(
			int initialCapacity,
			long maximumSize
	) {
		// Record 允许我们定义一个紧凑的构造函数来进行参数校验或设置默认值
		public Caffeine {
			if (initialCapacity < 0 || maximumSize <= 0) {
				throw new IllegalArgumentException("Caffeine 容量配置必须为正数");
			}
		}

		// 我们也可以提供一个无参构造函数，来设置 Record 的默认值
		public Caffeine() {
			this(128, 1024);
		}
	}

	// 主 Record 的构造函数，用于设置顶层属性的默认值
	public XksmsCacheProperties {
		// 如果 yml 中没有配置 caffeine 块，就使用其无参构造函数提供的默认值
		if (caffeine == null) {
			caffeine = new Caffeine();
		}
		// 如果 yml 中没有配置 ttl，给一个空 Map
		if (ttl == null) {
			ttl = Collections.emptyMap();
		}
	}

	// 提供一个无参构造函数，用于完全没有配置 xksms.cache 时的默认行为
	public XksmsCacheProperties() {
		this(true, true, new Caffeine(), Collections.emptyMap());
	}
}