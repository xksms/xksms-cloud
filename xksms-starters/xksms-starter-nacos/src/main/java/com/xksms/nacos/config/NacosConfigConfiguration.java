package com.xksms.nacos.config;

import com.alibaba.cloud.nacos.NacosConfigProperties;
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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "xksms.nacos", name = "config-enabled", havingValue = "true", matchIfMissing = true)
public class NacosConfigConfiguration {

	@Bean
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public static BeanPostProcessor nacosConfigPropertiesPostProcessor(NacosProperties xksmsNacosProperties) {
		return new BeanPostProcessor() {
			@Override
			public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
				if (bean instanceof NacosConfigProperties configProperties) {
					log.info("[xksms-starter-nacos] Customizing NacosConfigProperties using xksms.nacos properties...");

					configProperties.setServerAddr(xksmsNacosProperties.getServerAddr());
					configProperties.setFileExtension(xksmsNacosProperties.getFileExtension());
					configProperties.setUsername(xksmsNacosProperties.getUsername());
					configProperties.setPassword(xksmsNacosProperties.getPassword());
					// 优先使用专属配置，否则回退到全局配置
					String finalNamespace = StringUtils.hasText(xksmsNacosProperties.getConfigNamespace())
							? xksmsNacosProperties.getConfigNamespace()
							: xksmsNacosProperties.getNamespace();
					configProperties.setNamespace(finalNamespace);

					String finalGroup = StringUtils.hasText(xksmsNacosProperties.getConfigGroup())
							? xksmsNacosProperties.getConfigGroup()
							: xksmsNacosProperties.getGroup();
					configProperties.setGroup(finalGroup);

					// ===================================================================
					// 处理共享配置 (修正后的正确逻辑)
					// ===================================================================
					if (xksmsNacosProperties.getSharedConfigs() != null && xksmsNacosProperties.getSharedConfigs().length > 0) {
						// 1. 获取 Spring Cloud Alibaba 中用于存储共享配置的内部 List
						List<NacosConfigProperties.Config> officialSharedConfigs = configProperties.getSharedConfigs();

						// 2. 如果官方的 List 未初始化，则创建一个新的
						if (officialSharedConfigs == null) {
							officialSharedConfigs = new ArrayList<>();
						}

						// 3. 遍历我们自定义的共享配置
						for (NacosProperties.SharedConfig customSharedConfig : xksmsNacosProperties.getSharedConfigs()) {
							// 4. 创建一个官方的 Config 对象并填充数据
							NacosConfigProperties.Config officialConfig = new NacosConfigProperties.Config();
							officialConfig.setDataId(customSharedConfig.getDataId());
							// 如果共享配置自己没定义 group，就用最终的全局 group
							officialConfig.setGroup(StringUtils.hasText(customSharedConfig.getGroup()) ? customSharedConfig.getGroup() : finalGroup);
							officialConfig.setRefresh(customSharedConfig.isRefresh());

							// 5. 将官方 Config 对象添加到 List 中
							officialSharedConfigs.add(officialConfig);
						}

						// 6. 将处理后的 List 设置回官方的 Properties Bean
						configProperties.setSharedConfigs(officialSharedConfigs);
						log.info("[xksms-starter-nacos] {} shared configs have been programmatically added.", officialSharedConfigs.size());
					}

					log.info("[xksms-starter-nacos] Nacos Config configured with:Ip='{}', Namespace='{}', Group='{}'", xksmsNacosProperties.getServerAddr(), finalNamespace, finalGroup);
				}
				return bean;
			}
		};
	}
}