package com.xksms.user;

import com.xksms.user.config.UserServiceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 用户中心应用入口。
 */
@SpringBootApplication
@EnableConfigurationProperties(UserServiceProperties.class)
public class UserApplication {

    public static void main(String[] args) {
        System.setProperty("nacos.logging.default-config-enabled", "false");
        SpringApplication.run(UserApplication.class, args);
    }
}
