package com.xksms.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;

/**
 * [新一代] XK-SMS Redis 助手服务。
 *
 * 它是一个标准的 Spring Bean，通过依赖注入持有 RedisTemplate。
 * 它取代了传统的、反模式的静态 RedisUtils，提供了类型安全、可测试、健壮的 Redis 操作封装。
 */
public final class RedisHelper {
	//log
	private static final Logger log = LoggerFactory.getLogger(RedisHelper.class);

	private final RedisTemplate<String, Object> redisTemplate;

	public RedisHelper(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	// --- String (字符串) 操作 ---

	/**
	 * 写入一个键值对。
	 * @param key 键
	 * @param value 值
	 */
	public void set(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
	}

	/**
	 * 写入一个带过期时间的键值对。
	 * @param key 键
	 * @param value 值
	 * @param timeout 过期时间
	 */
	public void set(String key, Object value, Duration timeout) {
		redisTemplate.opsForValue().set(key, value, timeout);
	}

	/**
	 * 读取一个键，返回一个 Optional<T>。
	 * 这是 Java 现代的、避免 NullPointerException 的最佳实践。
	 *
	 * @param key 键
	 * @param type 期望返回的值的类型
	 * @return 包含值的 Optional，如果键不存在则为空
	 */

	public <T> Optional<T> get(String key, Class<T> type) {
		Object value = redisTemplate.opsForValue().get(key);

		// 1. 如果值本身就是 null，直接返回空 Optional
		if (value == null) {
			return Optional.empty();
		}

		// 2. [关键] 使用 type 参数进行运行时类型检查
		// Class.isInstance() 是进行类型检查的最安全、最标准的方式
		if (type.isInstance(value)) {
			// 3. 只有在检查通过后，才进行类型转换，此时的转换是 100% 安全的
			return Optional.of(type.cast(value));
		}

		// 4. 如果类型不匹配，这是一个危险信号，说明可能存在数据污染或逻辑错误
		// 我们不应该抛出异常让调用者崩溃，而是记录一条警告，并返回空 Optional，保证方法的健壮性
		log.warn("Redis alet! Key '{}' 的值类型为 '{}', 但业务期望的类型为 '{}'。可能存在数据污染，已作安全处理。",
				key, value.getClass().getName(), type.getName());
		return Optional.empty();
	}

	/**
	 * [新增][低级别] 读取一个键，返回一个原始的 Optional<Object>。
	 * 适用于调用者不关心具体类型，或者需要自己处理反序列化的场景。
	 * 注意：调用者有责任处理后续的类型转换。
	 *
	 * @param key 键
	 * @return 包含原始值的 Optional，如果键不存在则为空。
	 */
	public Optional<Object> get(String key) {
		Object value = redisTemplate.opsForValue().get(key);
		return Optional.ofNullable(value);
	}

	/**
	 * 删除一个键。
	 * @param key 键
	 * @return 是否成功删除
	 */
	public boolean delete(String key) {
		return redisTemplate.delete(key);
	}

	// --- Hash (哈希) 操作 ---
	// ... 可以在这里继续添加对 Hash, List, Set, ZSet 的封装 ...

	// --- 分布式锁 (示例) ---

	/**
	 * 尝试获取一个分布式锁。
	 * @param lockKey 锁的键
	 * @param requestId 请求ID (例如 UUID)，用于保证锁的可重入性和安全性
	 * @param expireTime 锁的过期时间
	 * @return 是否成功获取锁
	 */
	public boolean tryLock(String lockKey, String requestId, Duration expireTime) {
		return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime));
	}

	/**
	 * 释放一个分布式锁 (必须是持有者才能释放)。
	 * @param lockKey 锁的键
	 * @param requestId 请求ID
	 * @return 是否成功释放
	 */
	public boolean releaseLock(String lockKey, String requestId) {
		// 简单的 LUA 脚本保证“先比较、再删除”的原子性
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		// 此处需要配置并使用 RedisScript
		// 这是一个更复杂的实现，暂时作为示例
		return false; // 简化示例
	}
}