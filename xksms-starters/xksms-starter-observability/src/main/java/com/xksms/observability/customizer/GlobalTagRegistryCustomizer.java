package com.xksms.observability.customizer;


import com.xksms.observability.properties.ObservabilityProperties;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;

import java.util.Map;
import java.util.stream.Stream;

public class GlobalTagRegistryCustomizer implements MeterRegistryCustomizer<MeterRegistry> {

	private final ObservabilityProperties properties;

	public GlobalTagRegistryCustomizer(ObservabilityProperties properties) {
		this.properties = properties;
	}

	@Override
	public void customize(MeterRegistry registry) {
		Map<String, String> tags = properties.getTags();

		if (tags != null && !tags.isEmpty()) {
			registry.config().commonTags(
					tags.entrySet().stream()
							.flatMap(e -> Stream.of(e.getKey(), e.getValue()))
							.toArray(String[]::new)
			);
		}
	}

}
