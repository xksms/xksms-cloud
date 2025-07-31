# xksms-starter-nacos

基于 Nacos 3.0.2 的服务注册与发现 Starter，提供开箱即用的服务注册、配置管理功能。

## 功能特性

- ✅ 自动服务注册与发现
- ✅ 配置管理与动态刷新
- ✅ 健康检查集成
- ✅ 元数据管理
- ✅ 多环境支持
- ✅ 兼容 Spring Cloud Alibaba 2023.x 版本

## 快速开始

### 1. 引入依赖

```xml

<dependency>
    <groupId>com.xksms</groupId>
    <artifactId>xksms-starter-nacos</artifactId>
</dependency>
```

### 2. 配置文件

**方式一：使用默认配置（推荐）**

```yaml
spring:
  application:
    name: xksms-user
  config:
    import: optional:classpath:nacos-default.yml

xksms:
  nacos:
    server-addr: localhost:8848
    namespace: xksms-dev
    group: USER_GROUP
    metadata:
      version: 1.0.0
      zone: beijing
```

**方式二：手动配置**

```yaml
spring:
  application:
    name: xksms-user
  config:
    import:
      - optional:nacos:${spring.application.name}.yaml
      - optional:nacos:application-${spring.profiles.active:dev}.yaml
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: xksms-dev
        group: USER_GROUP
      config:
        server-addr: localhost:8848
        namespace: xksms-dev
        group: USER_GROUP
        file-extension: yaml
        import-check:
          enabled: false
```

### 3. 启动应用

应用启动后会自动注册到 Nacos，可在 Nacos 控制台查看服务列表。

## 配置说明

| 配置项                      | 默认值            | 说明          |
|--------------------------|----------------|-------------|
| xksms.nacos.enabled      | true           | 是否启用 Nacos  |
| xksms.nacos.server-addr  | localhost:8848 | Nacos 服务器地址 |
| xksms.nacos.namespace    | public         | 命名空间        |
| xksms.nacos.group        | DEFAULT_GROUP  | 分组          |
| xksms.nacos.cluster-name | DEFAULT        | 集群名称        |
| xksms.nacos.weight       | 1.0            | 服务权重        |

## 环境变量支持

- NACOS_ADDR: Nacos 服务器地址
- NACOS_NAMESPACE: 命名空间
- NACOS_GROUP: 分组
- NACOS_CLUSTER: 集群名称

## 注意事项

1. **Spring Cloud Alibaba 2023.x 版本要求**：必须在 `spring.config.import` 中显式声明 Nacos 配置导入
2. **配置文件命名规范**：
    - 应用配置：`${spring.application.name}.yaml`
    - 环境配置：`application-${profile}.yaml`
3. **可选配置**：使用 `optional:` 前缀表示配置文件可选，避免启动失败

## 故障排除

### 问题：APPLICATION FAILED TO START - missing nacos: entry

**解决方案**：

1. 确保配置了 `spring.config.import`
2. 或者设置 `spring.cloud.nacos.config.import-check.enabled=false`

### 问题：连接 Nacos 失败

**解决方案**：

1. 检查 Nacos 服务器是否启动
2. 确认网络连接和端口配置
3. 验证命名空间和分组配置
