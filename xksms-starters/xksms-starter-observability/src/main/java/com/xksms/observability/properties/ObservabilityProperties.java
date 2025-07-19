package com.xksms.observability.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "observability.metrics")
public class ObservabilityProperties {

	/**
	 * 全局标签
	 */
	private Map<String, String> tags = new HashMap<>();

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}
}
