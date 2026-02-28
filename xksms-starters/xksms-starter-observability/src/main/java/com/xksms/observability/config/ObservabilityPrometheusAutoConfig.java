package com.xksms.observability.config;


import com.xksms.observability.customizer.GlobalTagRegistryCustomizer;
import com.xksms.observability.properties.ObservabilityProperties;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 可观测性自动配置类（Prometheus/Micrometer）
 * 提供统一的全局标签定制器、指标注册等能力
 */
@AutoConfiguration
@ConditionalOnClass({MeterRegistry.class, MeterRegistryCustomizer.class})
@EnableConfigurationProperties({ObservabilityProperties.class})
public class ObservabilityPrometheusAutoConfig {

	@Bean
	@ConditionalOnMissingBean
	public MeterRegistryCustomizer<MeterRegistry> metricsCommonTagsCustomizer(ObservabilityProperties properties) {
		return new GlobalTagRegistryCustomizer(properties);
	}
}
