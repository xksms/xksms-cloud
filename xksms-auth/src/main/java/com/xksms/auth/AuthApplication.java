package com.xksms.auth;

import com.xksms.auth.config.AuthServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.xksms.user.api.feign")
@EnableConfigurationProperties(AuthServerProperties.class)
public class AuthApplication {
        public static void main(String[] args) {
                // 同样，禁用 Nacos 默认日志
                System.setProperty("nacos.logging.default-config-enabled", "false");
                SpringApplication.run(AuthApplication.class, args);
	}
}
