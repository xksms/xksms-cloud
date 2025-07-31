package com.xksms.nacos.config;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.xksms.nacos.properties.NacosProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "xksms.nacos", name = "enabled", havingValue = "true", matchIfMissing = true)
public class NacosDiscoveryConfiguration {

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public static BeanPostProcessor nacosDiscoveryPropertiesPostProcessor(NacosProperties xksmsNacosProperties) {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
				if (bean instanceof NacosDiscoveryProperties discoveryProperties) {
					log.info("[xksms-starter-nacos] Customizing NacosDiscoveryProperties using xksms.nacos properties...");

					// 核心逻辑：将我们自定义的属性值，强类型地设置给官方的 Properties Bean
					discoveryProperties.setServerAddr(xksmsNacosProperties.getServerAddr());
					discoveryProperties.setClusterName(xksmsNacosProperties.getClusterName());
					discoveryProperties.setWeight(xksmsNacosProperties.getWeight());
					discoveryProperties.setUsername(xksmsNacosProperties.getUsername());
					discoveryProperties.setPassword(xksmsNacosProperties.getPassword());
					if (xksmsNacosProperties.getMetadata() != null) {
						discoveryProperties.getMetadata().putAll(xksmsNacosProperties.getMetadata());
					}

					// 优先使用专属配置，否则回退到全局配置
					String finalNamespace = StringUtils.hasText(xksmsNacosProperties.getDiscoveryNamespace())
							? xksmsNacosProperties.getDiscoveryNamespace()
							: xksmsNacosProperties.getNamespace();
					discoveryProperties.setNamespace(finalNamespace);

					String finalGroup = StringUtils.hasText(xksmsNacosProperties.getDiscoveryGroup())
							? xksmsNacosProperties.getDiscoveryGroup()
							: xksmsNacosProperties.getGroup();
					discoveryProperties.setGroup(finalGroup);

					log.info("配置 Nacos 服务发现，命名空间: '{}', 分组: '{}'", finalNamespace, finalGroup);
				}
				return bean;
			}
		};
	}
}