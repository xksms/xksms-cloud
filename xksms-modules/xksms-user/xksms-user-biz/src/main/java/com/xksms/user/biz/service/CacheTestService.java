package com.xksms.user.biz.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class CacheTestService {

	/**
	 * 这是一个模拟“昂贵操作”的方法，例如一次复杂的数据库查询。
	 * 我们用 @Cacheable 注解来缓存它的返回结果。
	 *
	 * @param key 用来区分缓存的键
	 * @return 当前的 Unix 时间戳 (秒)
	 */
	@Cacheable(cacheNames = "test-cache", key = "#key")
	public long getTimestamp(String key) {
		log.info("========== Cache Miss! ==========");
		log.info("正在执行一个“昂贵”的操作，key: {}...", key);
		try {
			// 模拟 2 秒的耗时
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		long timestamp = Instant.now().getEpochSecond();
		log.info("“昂贵”的操作执行完毕，返回时间戳: {}", timestamp);
		return timestamp;
	}
}