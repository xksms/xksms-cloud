package com.xksms.nacos.config;

import com.xksms.nacos.properties.NacosProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

/**
 * Nacos 自动配置类
 * 作用：
 * 1. 启用自定义配置属性 NacosProperties
 * 2. 提供功能开关控制
 * 3. 统一导入相关配置类
 * 4. 启用服务发现客户端
 */
@Slf4j
@AutoConfiguration
@EnableDiscoveryClient
@EnableConfigurationProperties(NacosProperties.class)
@ConditionalOnProperty(prefix = "xksms.nacos", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({NacosDiscoveryConfiguration.class, NacosConfigConfiguration.class})
public class NacosAutoConfiguration {

	public NacosAutoConfiguration(NacosProperties nacosProperties) {
		log.info("初始化 xksms-starter-nacos，服务器地址: {}, 命名空间: {}",
				nacosProperties.getServerAddr(), nacosProperties.getNamespace());
	}
}