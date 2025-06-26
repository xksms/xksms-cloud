# xksms-starter-log

`xksms-starter-log` 是企业级微服务系统中的通用日志增强组件，负责统一日志格式、TraceId 传递和链路追踪信息注入，便于接入 ELK、SkyWalking、Prometheus 等可观测性系统。

---

## ✨ 特性

- ✅ 自动注入全局 TraceId（支持跨线程 & RPC 传递）
- ✅ 集成 MDC，上下文日志自动打印 userId、traceId
- ✅ 适配 Logstash JSON 格式，便于 ELK 搜索分析
- ✅ 提供 SPI 接口 `LogUserProvider`，可灵活获取当前登录用户

---

## 📦 依赖

```xml

<dependency>
    <groupId>com.xksms</groupId>
    <artifactId>xksms-starter-log</artifactId>
</dependency>
```

---

## 🧩 自动配置类

```java

@AutoConfiguration
@ConditionalOnClass(Logger.class)
public class LogAutoConfiguration {

	@Bean
	public FilterRegistrationBean<Filter> traceIdMdcFilter(LogUserProvider logUserProvider) {
		xxxxxx
	}

}
```

---

## 🧠 关键类说明

| 类/接口名                     | 说明                                  |
|---------------------------|-------------------------------------|
| `TraceIdMdcFilter`        | Servlet Filter，注入 MDC 内容（traceId 等） |
| `TraceIdGenerator`        | TraceId 生成器（默认 UUID，可扩展雪花）          |
| `LogConstant`             | MDC 字段常量（如 `X-Trace-Id`, `userId`）  |
| `LogUserProvider`（SPI 接口） | 日志模块用来获取当前用户 ID 的接口                 |

---

## 🔌 SPI 接口使用

项目需提供 `LogUserProvider` 实现，通常由安全模块 `xksms-starter-security` 提供：

```java
public class SecurityLogUserProvider implements LogUserProvider {
	@Override
	public String getCurrentUserId() {
		return LoginUserContext.getUserId(); // 从上下文中获取 userId
	}
}
```

并在 `resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 中注册：

```
com.xksms.security.spi.SecurityLogUserProvider
```

---

## 📄 MDC 日志输出示例

```json
{
  "timestamp": "2025-06-26T12:00:00",
  "level": "INFO",
  "thread": "http-nio-8080-exec-1",
  "logger": "com.xksms.demo.controller.HelloController",
  "message": "用户访问首页",
  "traceId": "9a60b0ef-c1a5-4f4e-89d2-b2293f3f9a62",
  "userId": "123456",
  "applicationName": "xksms-user-biz"
}
```

---

## 📂 目录结构

```
xksms-starter-log
├── config/LogAutoConfiguration.java        # 自动配置类
├── filter/TraceIdMdcFilter.java            # 注入 TraceId 的 Filter
├── utils/TraceIdGenerator.java             # TraceId 生成器
├── spi/LogUserProvider.java                # 当前用户获取 SPI 接口
├── constants/LogConstant.java              # MDC 字段常量
└── resources/
    └── META-INF/
        └── spring/
            └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

---

## 🧪 测试建议

- 使用 Postman 模拟请求，查看日志中是否正确输出 traceId 与 userId。
- 验证 SkyWalking、ELK 是否能正确采集和展示日志字段。

---

## 📝 版本说明

- 当前版本：`1.0.0-SNAPSHOT`
- 支持 Spring Boot 3.2.x / Java 17+
