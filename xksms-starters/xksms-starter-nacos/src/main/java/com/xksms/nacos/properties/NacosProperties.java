package com.xksms.nacos.properties;

import lombok.Data;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Role;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "xksms.nacos")
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class NacosProperties {

	// ===================================================================
	// 全局总开关
	// ===================================================================
	/**
	 * 是否启用 xksms-starter-nacos
	 */
	private boolean enabled = true;

	// ===================================================================
	// 通用/共享配置 (作为默认值)
	// ===================================================================
	/**
	 * Nacos 服务器地址
	 */
	private String serverAddr = "localhost:8848";

	/**
	 * Nacos 访问用户名
	 */
	private String username;

	/**
	 * Nacos 访问密码或 Token
	 */
	private String password;

	/**
	 * 命名空间，用于环境隔离。此为全局默认值。
	 */
	private String namespace;

	/**
	 * 分组。此为全局默认值。
	 */
	private String group = "DEFAULT_GROUP";

	// ===================================================================
	// 服务发现 (Discovery) 专属配置
	// ===================================================================
	/**
	 * 【可选】单独为服务发现指定命名空间。如果未设置，则使用全局的 'namespace'。
	 */
	private String discoveryNamespace;

	/**
	 * 【可选】单独为服务发现指定分组。如果未设置，则使用全局的 'group'。
	 */
	private String discoveryGroup;

	/**
	 * 集群名称
	 */
	private String clusterName = "DEFAULT";

	/**
	 * 服务权重
	 */
	private float weight = 1.0F;

	/**
	 * 服务元数据
	 */
	private Map<String, String> metadata;

	// ===================================================================
	// 配置中心 (Config) 专属配置
	// ===================================================================
	/**
	 * 是否启用配置管理
	 */
	private boolean configEnabled = true;

	/**
	 * 【可选】单独为配置中心指定命名空间。如果未设置，则使用全局的 'namespace'。
	 */
	private String configNamespace;

	/**
	 * 【可选】单独为配置中心指定分组。如果未设置，则使用全局的 'group'。
	 */
	private String configGroup;

	/**
	 * 配置文件扩展名
	 */
	private String fileExtension = "yaml";

	/**
	 * 共享配置
	 */
	private SharedConfig[] sharedConfigs;

	@Data
	public static class SharedConfig {
		private String dataId;
		private String group;
		private boolean refresh = true;
	}
}