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

	/**
	 * 【新增】本地开发时，供Filebeat采集的日志文件输出路径 (绝对路径)
	 */
	private String filePath;
}