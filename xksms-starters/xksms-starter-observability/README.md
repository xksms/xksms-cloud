# xksms-starter-observability

企业级 Spring Boot 微服务系统统一指标监控组件，封装 Prometheus + Micrometer 接入逻辑，支持通用标签配置、自动暴露监控端点。

## 功能特性

- ✅ 自动配置 Micrometer + Prometheus
- ✅ 默认暴露 /actuator/prometheus 监控端点
- ✅ 支持配置导入：无侵入引入默认观测配置
- ✅ 支持动态注入通用标签（如服务名、环境、IP、区域）
- ✅ 完整兼容 Spring Boot 3.x

## 模块结构

```
xksms-starter-observability
├── config
│   ├── ObservabilityMetricsProperties.java  # 配置属性
│   └── ObservabilityAutoConfiguration.java  # 自动配置入口
├── resources
│   └── META-INF
│       └── spring.factories                # Spring Boot 自动装配入口
│   └── observability-default.yml           # 默认观测配置
```

## 1. 引入依赖

```
<dependency>
  <groupId>com.xksms</groupId>
  <artifactId>xksms-starter-observability</artifactId>
  <version>1.0.0</version>
</dependency>
```

## 2. 添加配置（推荐方式：配置导入）

```
application.yml:

spring:
  application:
    name: xksms-user
  config:
    import: optional:classpath:observability-default.yml

observability:
  metrics:
    tags:
      region: huabei-1
```

## 3. 默认配置（由 Starter 提供）

```
observability-default.yml:

management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    prometheus:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
      env: ${spring.profiles.active:dev}
      ip: 127.0.0.1
```

## 4. 动态标签注入示例

```
@Bean
public MeterRegistryCustomizer<MeterRegistry> registryCustomizer(ObservabilityMetricsProperties properties) {
    return registry -> registry.config().commonTags(
        properties.getTags().entrySet().stream()
            .map(e -> Tag.of(e.getKey(), e.getValue()))
            .collect(Collectors.toList())
    );
}
```

## 5. 验证方式

访问地址：

http://localhost:8080/actuator/prometheus

若正确接入，应能看到类似如下内容：

```
# HELP jvm_memory_used_bytes ...
jvm_memory_used_bytes{area="heap",id="G1 Eden Space",...} 1.048576E7
```

## 6. Prometheus 配置示例

```
docker-compose.yml:

version: '3'
services:
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    ports:
      - "9090:9090"
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml

prometheus.yml:

global:
  scrape_interval: 15s

scrape_configs:
  - job_name: "xksms"
    metrics_path: "/actuator/prometheus"
    static_configs:
      - targets: ["host.docker.internal:8080"]
```

## 7. 搭配 Grafana 可视化

连接 Prometheus 数据源，导入 JVM、Spring Boot、Tomcat Dashboard 即可展示服务运行指标图表。

## 参考链接

- https://micrometer.io/docs
- https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html
- https://prometheus.io
