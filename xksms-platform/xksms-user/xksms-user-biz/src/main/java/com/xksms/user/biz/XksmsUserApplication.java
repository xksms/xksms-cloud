package com.xksms.user.biz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class XksmsUserApplication {
	public static void main(String[] args) {
		// 禁用 Nacos 默认日志配置
		System.setProperty("nacos.logging.default-config-enabled", "false");
		SpringApplication.run(XksmsUserApplication.class, args);
		log.info("XksmsUserApplication started...");
	}

}
