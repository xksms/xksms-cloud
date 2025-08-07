# 📘《XKSMS 命名空间与属性治理规范（V1.1 修订草案）》

---

## 一、制定目标

本规范旨在统一 XKSMS 系统中所有用于：

* 请求上下文（如 `ServerWebExchange#getAttributes`）
* HTTP 请求头 / 响应头
* 日志 MDC 上下文字段
* **【新增】可观测性指标（Metrics）**
* JSON 字段（如异常响应中的 traceId）

的命名方式，确保命名 **语义清晰、边界分明、避免冲突、便于追踪**，从而提升系统的稳定性和可维护性。

---

## 二、命名原则

| 维度                            | 命名规范          | 命名风格             | 示例                            |
|-------------------------------|---------------|------------------|-------------------------------|
| **内部上下文属性**<br>（如 attributes） | 带命名空间前缀       | dot.case（点分）     | `xksms.trace_id`              |
| **HTTP Header（请求头/响应头）**      | 标准协议或短横线      | kebab-case（短横线）  | `x-trace-id`、`traceparent`    |
| **日志上下文 MDC key**             | 与 Header 保持一致 | kebab-case       | `x-trace-id`                  |
| **【新增】Micrometer 指标名**        | **点分命名法**     | **dot.case（点分）** | **`xksms.user.logins.total`** |
| **JSON 字段名**                  | 驼峰命名          | camelCase        | `traceId`、`userId`            |

---

## 三、内部属性命名规范（`exchange.getAttributes()`）

### ✅ 命名规则

* 统一使用前缀：`xksms.` 作为命名空间
* 用 `dot.case` 表示层级关系
* 后缀必须有清晰语义（如 trace\_id、user\_id、tenant\_id）

### ✅ 推荐 Key 示例

| Key                   | 用途说明               |
|-----------------------|--------------------|
| `xksms.trace_id`      | 当前请求的唯一 traceId    |
| `xksms.user_id`       | 当前登录用户 ID          |
| `xksms.tenant_code`   | 多租户代码              |
| `xksms.locale`        | 当前国际化语言            |
| `xksms.token_payload` | 解码后的 JWT 内容对象（Map） |

### 🚫 禁止示例

| 错误命名       | 问题          |
|------------|-------------|
| `traceId`  | 容易与其他库冲突    |
| `user`     | 语义模糊，含义不清   |
| `tid`      | 命名过短，缺乏语义表达 |
| `trace_id` | 无命名空间前缀，易冲突 |

---

## 四、HTTP Header 命名规范

### ✅ 命名规则

* 使用行业标准或事实标准命名
* 自定义 header 统一以 `x-` 前缀开头
* 使用 `kebab-case`（短横线风格）

### ✅ 推荐字段

| Header          | 说明                            |
|-----------------|-------------------------------|
| `x-trace-id`    | 自定义追踪 ID，方便人读与日志追踪            |
| `traceparent`   | W3C 标准链路追踪 header             |
| `x-user-id`     | 前端或第三方调用传入用户 ID               |
| `x-tenant-code` | 多租户标识                         |
| `sw8`           | SkyWalking Agent 自动注入，不建议手动维护 |

---

## 五、日志 MDC 命名规范

### ✅ 命名规则

* MDC 中的 key 必须与请求头一致
* 使用 `kebab-case`
* 保证所有日志打印时可通过 `%X{}` 输出

### ✅ 推荐配置（logback-spring.xml）

```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{x-trace-id}] %logger{36} - %msg%n</pattern>