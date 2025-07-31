// 位于: xksms-starter-log 模块
package com.xksms.log.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "logstash")
public class LogstashProperties {

	/**
	 * Logstash 服务器地址
	 */
	private String host = "localhost";

	/**
	 * Logstash 服务器端口
	 */
	private int port = 4560;
}