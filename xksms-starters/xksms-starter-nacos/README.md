# xksms-starter-nacos

`xksms-starter-nacos` 是 `xksms-cloud` 微服务平台的核心基础组件之一。它基于 Spring Cloud Alibaba Nacos，提供了开箱即用的服务注册、服务发现和配置管理功能，并通过统一的配置模型，极大地简化了业务服务的接入成本。

## 🧠 设计哲学

我们构建此 Starter 的核心目标，并不仅仅是“引入Nacos”，而是要实现以下平台级目标：

- **统一配置入口**：所有 Nacos 相关配置收敛至 `xksms.nacos.*` 前缀下，形成平台统一的品牌和开发体验。
- **约定优于配置**：通过内置 `spring-cloud-starter-bootstrap`，我们解决了 Spring Boot 启动生命周期问题，使得开发者无需关心复杂的引导配置，实现了真正统一的配置模式。
- **封装复杂性**：将原生配置的动态映射、客户端的定制等复杂逻辑封装在 Starter 内部，为业务开发者提供最简洁的接口。
- **增强灵活性**：在保持简洁的同时，提供了如 `discoveryNamespace`、`configGroup` 等专属配置，以及 `sharedConfigs` 等高级功能，满足复杂场景的需求。

## ✨ 功能特性

- ✅ **自动装配**：仅需引入依赖并提供少量核心配置即可工作。
- ✅ **统一配置模型**：通过 `xksms.nacos.*` 前缀统一管理服务发现和配置中心。
- ✅ **智能回退机制**：支持为服务发现和配置中心设置专属的 `namespace` 和 `group`，如果未设置，则自动回退使用全局配置。
- ✅ **类型安全的共享配置**：通过结构化的 `sharedConfigs` 属性，优雅地实现多共享配置的加载。
- ✅ **内置引导上下文**：自动引入 `spring-cloud-starter-bootstrap`，确保配置在应用启动的最早期阶段被正确加载，对使用者透明。
- ✅ **诊断日志**：在应用启动时，打印出最终生效的 Nacos 关键配置，便于排查问题。

## 🚀 快速开始

### 1. 引入依赖

在你的业务模块（如 `xksms-user-biz`）的 `pom.xml` 中引入本 Starter。
```xml
<dependency>
    <groupId>com.xksms</groupId>
    <artifactId>xksms-starter-nacos</artifactId>
   <version>${revision}</version>
</dependency>
````

### 2\. 添加配置

在业务模块的 `application.yml` 中，添加 `xksms.nacos` 相关配置。这是唯一推荐的配置方式。

```yaml
xksms:
  nacos:
     # [必须] Nacos 服务器地址，指向核心服务端口 8848
     server-addr: 192.168.2.10:8848
     # [必须] Nacos 命名空间，用于环境隔离
     namespace: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
     # [可选] Nacos 分组
     group: DEFAULT_GROUP
     # [如果Nacos开启认证，则必须]
     username: nacos
     password: your_nacos_auth_token
```

> **注意**：无需再配置任何 `spring.cloud.nacos.*` 或 `spring.config.import` 相关的属性，本 Starter 已全部为您处理。

## ⚙️ 配置属性详解

以下是所有可用的配置项，均位于 `xksms.nacos` 前缀下。

### 基础配置

| 配置项           | 描述                              | 默认值              |
|---------------|---------------------------------|------------------|
| `enabled`     | 是否启用 `xksms-starter-nacos` 的功能。 | `true`           |
| `server-addr` | Nacos 服务器地址，多个地址用逗号`,`分隔。       | `localhost:8848` |
| `username`    | Nacos 访问用户名。                    | `null`           |
| `password`    | Nacos 访问密码或 Token。              | `null`           |
| `namespace`   | 全局默认的命名空间，用于环境隔离。               | `null`           |
| `group`       | 全局默认的分组。                        | `DEFAULT_GROUP`  |

### 服务发现 (Discovery) 配置

| 配置项                   | 描述                                               | 默认值       |
|-----------------------|--------------------------------------------------|-----------|
| `discovery-namespace` | **[可选]** 单独为服务发现指定命名空间。如果未设置，则使用全局的 `namespace`。 | `null`    |
| `discovery-group`     | **[可选]** 单独为服务发现指定分组。如果未设置，则使用全局的 `group`。       | `null`    |
| `cluster-name`        | 实例所属的集群名称。                                       | `DEFAULT` |
| `weight`              | 实例的权重。                                           | `1.0`     |
| `metadata`            | 实例的元数据，Map 结构。例如：`metadata.version: 1.0.0`       | `null`    |

### 配置中心 (Config) 配置

| 配置项                | 描述                                               | 默认值    |
|--------------------|--------------------------------------------------|--------|
| `config-enabled`   | 是否启用配置中心功能。                                      | `true` |
| `config-namespace` | **[可选]** 单独为配置中心指定命名空间。如果未设置，则使用全局的 `namespace`。 | `null` |
| `config-group`     | **[可选]** 单独为配置中心指定分组。如果未设置，则使用全局的 `group`。       | `null` |
| `file-extension`   | Nacos 中配置文件的扩展名。                                 | `yaml` |
| `shared-configs`   | 共享配置列表。这是一个对象数组。                                 | `null` |

## 💡 高级用法：共享配置

通过 `shared-configs` 可以让一个服务加载多个共享的配置文件，非常适合用于抽取像数据库、Redis 等公共配置。

### 示例

假设在 Nacos 的 `DEFAULT_GROUP` 中有两个共享配置：`common.yml` 和 `datasource.yml`。

在 `xksms-user` 服务的 Nacos 配置文件（例如 `xksms-user-dev.yml`）中可以这样配置：

```yaml
xksms:
   nacos:
      shared-configs:
         - data-id: common.yml   # 加载 common.yml，使用默认 group 和动态刷新
         - data-id: datasource.yml
           group: COMMON_GROUP   # 也可以为某个共享配置指定一个不同的 group
           refresh: false        # 这个共享配置不动态刷新
```

## 📝 版本兼容

- **Spring Boot**: 3.2.x
- **Spring Cloud**: 2023.0.x
- **Spring Cloud Alibaba**: 2023.0.x
- **Nacos Server**: 2.x / 3.x

<!-- end list -->

```
```