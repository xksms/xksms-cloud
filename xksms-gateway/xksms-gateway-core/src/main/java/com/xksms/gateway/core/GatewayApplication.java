package com.xksms.gateway.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * xksms-cloud 网关核心服务启动类
 */
@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		// 为了兼容Nacos 3.x，禁用其默认的日志配置，由我们自己的 xksms-starter-log 统一管理
		System.setProperty("nacos.logging.default-config-enabled", "false");
		SpringApplication.run(GatewayApplication.class, args);
	}

}