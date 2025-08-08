package com.xksms.observability.config;


import com.xksms.observability.customizer.GlobalTagRegistryCustomizer;
import com.xksms.observability.properties.ObservabilityProperties;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties({ObservabilityProperties.class})
public class ObservabilityPrometheusAutoConfig {

	@Bean
	@ConditionalOnMissingBean
	public MeterRegistryCustomizer<MeterRegistry> metricsCommonTagsCustomizer(ObservabilityProperties properties) {
		return new GlobalTagRegistryCustomizer(properties);
	}
}
