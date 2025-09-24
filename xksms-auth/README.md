# XK-SMS 统一认证授权中心

本文档描述了 `xksms-auth` 模块的企业级 SaaS 认证授权体系架构、关键组件以及运维要点，帮助开发、运维及安全团队快速理解系统能力并进行二次扩展。

## 架构演进路线

### 阶段一：奠定基石 —— 认证与用户管理彻底解耦

* `xksms-auth` 仅负责凭证校验与令牌签发，内部保持无状态，通过 Feign 调用 `xksms-user` 获取真实的账号信息。
* `xksms-user` 服务专注于账号、资料与权限等敏感数据的持久化存储，屏蔽授权中心对业务库的直接访问。
* 解耦后的安全边界更清晰：认证层关注协议和令牌，用户层聚焦数据治理，易于在未来扩展短信、企业微信、钉钉等多种登录方式。

### 阶段二：确立边界 —— API 与 Biz 契约分离

* 每个服务最少拆分为 `*-api` 与 `*-biz` 两个模块：`api` 提供 DTO、Feign 契约与常量，`biz` 落地具体 Controller/Service/Repository。
* 调用方只需要依赖轻量的 `api` 模块即可编译通过，避免引入对端实现层的数据库或中间件依赖，物理上阻断循环依赖。
* 契约优先迫使团队在编码前梳理清楚业务边界，`api` 的演进直接映射到跨服务的接口变更。

### 阶段三：架构升维 —— 多租户 SaaS 数据模型

* 以租户为第一公民，抽象 `sys_tenant` 等顶层实体，将账户、资料、权限拆分成独立的数据结构并通过租户外键串联。
* 关键业务表统一补全审计字段（`creator_id`/`creator_name`/`updater_id`/`updater_name`）与乐观锁版本号，确保企业级的可追溯性与并发控制。
* 全部结构变更通过 Flyway 版本化脚本管理，让应用代码与数据库形态保持一致，降低多环境部署风险。

### 阶段四：领域驱动 —— 目录即架构

* 平台级能力（认证、用户、系统、通知等）统一归档到 `xksms-platform` 目录，领域业务模块沉淀在 `xksms-modules`，物理结构即依赖关系。
* `xksms-modules` 下仍沿用 `api`/`biz` 双层划分，确保业务服务消费平台能力但不会反向侵入。
* 清晰的目录规划为平台团队与业务团队的协同提供天然边界，避免架构腐化。

## 1. 架构概览

统一认证授权中心基于 Spring Authorization Server 构建，结合多租户上下文隔离、租户级客户端与用户管理以及 SaaS 常见的信任域扩展能力，形成如下逻辑分层：

```
┌──────────────────────────────────────────────────────┐
│ Presentation (Interfaces)                            │
│  • /oauth2/** 标准协议端点                           │
│  • /tenants/** 租户发现接口                           │
├──────────────────────────────────────────────────────┤
│ Application & Security                               │
│  • TenantContextFilter 解析租户上下文                │
│  • TenantAwareUserDetailsService 统一鉴权入口        │
│  • AuthorizationServerConfigurer OAuth2 协议装配      │
├──────────────────────────────────────────────────────┤
│ Domain                                               │
│  • TenantId / Tenant 值对象                          │
│  • UserAccount 聚合                                   │
│  • TenantUserAccountService 领域服务                  │
├──────────────────────────────────────────────────────┤
│ Infrastructure                                       │
│  • InMemoryTenantRegisteredClientRepository          │
│  • CompositeTenantUserAccountService (远程 + 本地)    │
│  • TenantRegistry / Feign 拦截器 / Filter             │
└──────────────────────────────────────────────────────┘
```

## 2. 核心能力

### 2.1 多租户上下文感知

* `TenantContextFilter` 从请求头 `X-Tenant-Id`、查询参数或二级域名提取租户标识，验证后写入 `TenantContextHolder`。
* `TenantFeignRequestInterceptor` 将当前租户透传至下游（例如用户中心），保证跨服务调用的租户隔离。
* `TenantRegistry` 基于配置的 `xksms.auth.tenants` 初始化租户元数据，并支持对外查询。

### 2.2 统一用户认证链路

* `TenantAwareUserDetailsService` 负责将领域内的 `UserAccount` 转换为 Spring Security 所需的 `UserDetails`。
* `CompositeTenantUserAccountService` 优先调用用户中心获取账号信息，发生异常或无数据时自动回退到配置化的 `InMemoryTenantUserAccountService`，提升稳定性。
* 密码、角色、账号状态等安全属性全部在 `UserAccount` 聚合中统一维护，便于审计与扩展。

### 2.3 客户端与协议支持

* `InMemoryTenantRegisteredClientRepository` 根据租户配置动态创建 OAuth2 客户端，自动处理密钥编码、授权方式、Scope 等设置，并为每个客户端附加租户隔离 Scope（`tenant:{tenantId}`）。
* `AuthorizationServerConfig` 装配 Authorization Server 过滤器链、JWK 密钥对与元信息，支持自定义 `issuer`。OAuth2 Resource Server 能力默认开启 JWT 验证。

### 2.4 对外能力与运营支持

* `/tenants` 接口输出租户清单，供运营后台或前端应用实现“选择租户”功能。
* `application.yml` 支持为不同租户声明客户端、引导用户、令牌 TTL 等属性，可按环境覆盖。
* `xksms-common` 新增的 `Result` 统一了跨服务通信协议，保证与用户中心的契约一致。

## 3. 关键流程

1. **租户解析**：用户访问认证端点时，`TenantContextFilter` 解析并校验租户，失败请求直接返回 400。
2. **客户端鉴别**：`InMemoryTenantRegisteredClientRepository` 根据租户查找客户端，实现租户级隔离。
3. **用户认证**：`TenantAwareUserDetailsService` 触发 `CompositeTenantUserAccountService` 拉取账号信息，优先远程、失败回退本地。
4. **令牌发放**：Authorization Server 按租户配置的 Token TTL、Scope 等策略生成令牌，默认使用内存 JWK，可替换为外部密钥管理。

## 4. 配置示例

```yaml
xksms:
  auth:
    issuer: http://localhost:9999
    default-tenant: public
    tenants:
      - id: public
        name: 公共云默认租户
        clients:
          - client-id: messaging-client
            client-secret: messaging-secret
            grant-types: [authorization_code, refresh_token]
            scopes: [openid, profile, message.read]
            redirect-uris:
              - http://localhost:8080/login/oauth2/code/messaging-client-oidc
            token:
              access-token-ttl: PT30M
        bootstrap-users:
          - username: admin
            password: Admin@123
            authorities: [ROLE_PLATFORM_ADMIN]
```

> `password` 支持明文（启动时自动使用 BCrypt 编码）或 `{bcrypt}***` 形式的预编码密钥。

## 5. 扩展建议

* **持久化实现**：替换 `InMemoryTenantRegisteredClientRepository` 与 `CompositeTenantUserAccountService` 可接入数据库或配置中心。
* **审计与风控**：可基于 `TenantContextHolder` 扩展审计日志、异常登录报警、登录限流等能力。
* **OIDC 扩展**：可通过 `OAuth2AuthorizationServerConfigurer` 自定义 ID Token Claims，兼容更多 SaaS 场景。

## 6. 运行与验证

1. `mvn -pl xksms-auth spring-boot:run` 启动服务。
2. 调用 `GET /tenants` 校验租户配置是否加载成功。
3. 使用 Postman/浏览器执行标准 OAuth2 授权码流程，验证不同租户的客户端与用户隔离策略。

## 7. 目录结构

```
xksms-auth
├── README.md                本文档
├── pom.xml
└── src/main/java/com/xksms/auth
    ├── AuthApplication.java         启动类，加载配置
    ├── config/                      Spring 配置与属性定义
    ├── domain/                      领域模型（租户、用户）
    ├── infrastructure/              多租户上下文、远程调用、OAuth2 仓储
    ├── interfaces/rest/             对外租户查询接口
    └── service/                     安全适配服务
```

至此，企业级 SaaS 认证授权模块的骨架、核心流程与关键代码均已落地，可直接作为多租户业务的统一身份基座使用。
