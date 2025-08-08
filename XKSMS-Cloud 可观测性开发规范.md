# XKSMS-Cloud 可观测性开发规范

## 1. 目标与原则

本规范旨在确保 `xksms-cloud` 微服务体系具备**端到端、无断点**的链路追踪能力。我们统一采用基于 **Micrometer** 与 **OpenTelemetry (OTel)** 的标准化观测模型，将可观测性内化为开发流程的核心环节，而非事后补救。

**核心原则**：任何引入系统的 I/O 操作都必须是**可观测**的。

## 2. 核心开发规范

### 规则：新 I/O 依赖引入必须验证链路完整性

> 当向项目中引入任何会产生网络或磁盘 I/O 的新依赖时（例如：数据库驱动、消息队列客户端、HTTP 客户端、缓存客户端、对象存储 SDK 等），**开发人员有责任验证其操作调用是否已被 Micrometer Tracing 自动支持**。
>
> 如果不被自动支持，**必须通过 `@Observed` 注解或编程式 API 手动包裹其调用，确保链路的完整性**。

此项规范是代码审查（Code Review）中的**强制检查点**。

## 3. 操作流程与最佳实践

### 3.1. 如何验证依赖的自动追踪能力？

在引入新的 I/O 依赖后，请遵循以下三步进行验证：

1. **查阅文档 (Do Your Research)**
    * 优先查阅 [Spring Boot Actuator 支持的自动配置](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.observability.auto-configuration)
      和 [Micrometer Tracing Instrumentation](https://micrometer.io/docs/tracing#_supported_instrumentations) 的官方文档，确认新库是否在官方支持列表中。

2. **本地测试 (Trust, but Verify)**
    * 在开发环境中，构造一个完整的业务场景，使其调用通过新引入的依赖。
    * 打开 SkyWalking 或你选择的观测系统 UI，检查该调用的链路拓扑图。
    * **关键判断**：如果链路在调用新依赖的操作后中断，或者未能生成对应的 Span，即可判定为**不被自动支持**。

3. **代码审查 (Enforce the Standard)**
    * 在提交合并请求（Pull Request）时，如果引入了新的 I/O 依赖，必须在 PR 描述中明确说明其链路追踪的支持情况和你的验证结果。
    * 审查者（Reviewer）有责任对这一点进行确认。

### 3.2. 如何进行手动埋点？

当确认需要手动埋点时，我们**首选**使用 Micrometer 提供的 `@Observed` 注解，因为它以声明式的方式将观测逻辑与业务代码优雅地结合。

**最佳实践示例：**

假设我们引入了一个自定义的 `CustomFileClient` 用于与第三方文件服务交互，它不在自动支持列表内。

```java
// 位置: xksms-file-biz 模块的某个 Service 中
package com.xksms.file.service;

import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

@Service
public class CustomFileStorageService {

	private final CustomFileClient customFileClient;

	public CustomFileStorageService(CustomFileClient customFileClient) {
		this.customFileClient = customFileClient;
	}

	/**
	 * 使用 @Observed 注解包裹一个不被自动追踪的方法调用。
	 *
	 * @param bucketName 桶名称
	 * @param fileName 文件名
	 * @param content 文件内容
	 */
	@Observed(
			// `name` 定义了 Span 的名字，应简短且具有描述性，使用点分格式。
			name = "file.storage.upload",
			// `contextualName` 是一个更具体的、在当前上下文中的名字。
			contextualName = "upload-avatar-to-cloud",
			// `lowCardinalityKeyValues` 用于定义 Span 的标签（Tag），必须是键值对。
			// key 和 value 成对出现。这里的 key 应该是低基数的，即不会有太多唯一值。
			lowCardinalityKeyValues = {"storage.provider", "customCloud", "file.type", "image"}
	)
	public void uploadAvatar(String bucketName, String fileName, byte[] content) {
		// 这个方法的整个执行过程，现在都会被 Micrometer 观测，并生成一个名为 "file.storage.upload" 的 Span。
		customFileClient.upload(bucketName, fileName, content);
	}
}
```

**注解参数说明:**

* `name`: **必须提供**。作为 Span 的名字，是其主要标识。命名应采用 `.` 分隔的格式，体现类别和操作，如 `db.query`, `http.request`。
* `contextualName`: 可选。在具体调用上下文中的一个更友好的名字，有助于在追踪详情中快速理解业务场景。
* `lowCardinalityKeyValues`: **强烈推荐**。用于为 Span 添加业务维度的标签，便于后续的筛选、聚合和分析。**必须成对出现** (`key1`, `value1`, `key2`, `value2`, ...)，且 Key 的取值范围不宜过大（例如，用 `userId`
  作为 key 是不合适的）。

---