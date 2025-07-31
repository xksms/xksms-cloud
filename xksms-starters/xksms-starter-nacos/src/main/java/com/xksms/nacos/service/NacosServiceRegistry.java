package com.xksms.nacos.service;

import com.xksms.nacos.properties.NacosProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NacosServiceRegistry {

	private final NacosProperties nacosProperties;

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		log.info("应用启动完成，Nacos 服务注册信息:");
		log.info("  服务器地址: {}", nacosProperties.getServerAddr());
		log.info("  命名空间: {}", nacosProperties.getNamespace());
		log.info("  分组: {}", nacosProperties.getGroup());
		log.info("  集群: {}", nacosProperties.getClusterName());
		log.info("  权重: {}", nacosProperties.getWeight());

		if (nacosProperties.getMetadata() != null && !nacosProperties.getMetadata().isEmpty()) {
			log.info("  元数据: {}", nacosProperties.getMetadata());
		}
	}
}