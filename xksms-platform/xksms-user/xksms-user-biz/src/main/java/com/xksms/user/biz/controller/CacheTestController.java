package com.xksms.user.biz.controller; // 假设你的 controller 包在这个位置

import com.xksms.user.biz.service.CacheTestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CacheTestController {

	private final CacheTestService cacheTestService;

	public CacheTestController(CacheTestService cacheTestService) {
		this.cacheTestService = cacheTestService;
	}

	@GetMapping("/cache-test")
	public String testCache(@RequestParam("myKey") String myKey) {
		long startTime = System.currentTimeMillis();
		long timestamp = cacheTestService.getTimestamp(myKey);
		long duration = System.currentTimeMillis() - startTime;
		log.info("获取到时间戳: {}, 本次调用耗时: {} ms", timestamp, duration);

		return String.format("获取到的时间戳是: %d, 本次调用耗时: %d ms", timestamp, duration);
	}
}