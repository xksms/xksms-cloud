# XK-SMS 统一认证授权中心

基于 Spring Authorization Server 构建的 OAuth 2.1/OIDC 统一认证授权中心，负责为平台内的微服务提供统一的认证、授权、令牌签发与校验能力。

## 功能特性

- **授权服务端点**：开箱即用的授权码模式、客户端模式、刷新令牌等能力，支持 OIDC 元数据发布。
- **远程用户认证**：通过 `RemoteUserDetailsService` 联动用户中心，按需加载用户密码、权限等信息。
- **安全基线配置**：统一开放 `/oauth2/**`、`/.well-known/**`、`/actuator/**` 等必须端点，其余请求均需完成认证。
- **标准化响应**：与平台统一的 `Result<T>` 响应模型及错误码体系保持一致，便于链路排查与前后端协作。

## 模块结构

```
xksms-auth
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java
    │   │   └── com/xksms/auth
    │   │       ├── AuthApplication.java          # 应用启动类，开启 OpenFeign
    │   │       ├── config
    │   │       │   ├── AuthorizationServerConfig.java  # 授权服务器核心配置
    │   │       │   └── SecurityConfig.java             # Spring Security 通用安全配置
    │   │       └── service
    │   │           └── RemoteUserDetailsService.java   # 远程用户信息加载服务
    │   └── resources
    │       └── application.yml
```

## 关键流程说明

1. **用户访问授权端点**：浏览器访问 `/oauth2/authorize` 或客户端通过 `/oauth2/token` 申请令牌。
2. **认证流程**：`SecurityConfig` 配置的表单登录负责用户名/密码认证，认证数据委托给 `RemoteUserDetailsService`。
3. **远程用户加载**：
    - `RemoteUserDetailsService` 使用 `UserFeignClient` 调用用户服务 `/users/internal/details/{username}` 接口。
    - 校验调用结果是否成功，并将返回的 `UserAuthDTO` 转换为 Spring Security 的 `UserDetails` 对象。
    - 未获取到有效信息或接口异常时抛出 `UsernameNotFoundException`，由 Spring Security 统一处理。
4. **令牌签发**：`AuthorizationServerConfig` 负责注册客户端、生成 RSA 密钥对并发布至 `/oauth2/jwks`，下游服务通过该端点获取公钥校验 JWT。

## 本地启动指南

1. **准备依赖服务**
    - 用户服务 (`xksms-user`) 需要提供 `/users/internal/details/{username}` 接口。
    - Nacos/Redis/日志等基础设施可按需启动（`application.yml` 中提供了默认地址，可根据环境调整）。

2. **运行项目**

   ```bash
   mvn -pl xksms-auth -am spring-boot:run
   ```

3. **验证授权流程**

    - 访问 `http://localhost:9999/oauth2/authorize?response_type=code&client_id=messaging-client&redirect_uri=http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc&scope=openid`。
    - 使用用户服务返回的账号密码登录，完成授权后即可在回调地址获取授权码。
    - 调用 `/oauth2/token` 交换访问令牌，或访问 `/.well-known/openid-configuration` 查看 OIDC 元数据。

## 常见问题

| 问题                    | 排查建议                                            |
|-----------------------|-------------------------------------------------|
| 用户登录失败，日志提示“用户服务暂不可用” | 检查用户服务是否启动、Feign 调用链路是否打通，或查看网关/注册中心配置。         |
| 登录成功但无权限访问业务接口        | 确认用户服务返回的 `authorities` 集合是否包含所需角色/权限标识。        |
| 无法获取 JWKS             | 检查授权中心是否正常生成 RSA 密钥对，确认 `/oauth2/jwks` 端点是否可访问。 |

## 后续规划

- 接入数据库存储客户端信息及授权码。
- 支持多租户、多终端的登录策略与会话管理。
- 提供统一的单点登录/登出、账号锁定等高级能力。