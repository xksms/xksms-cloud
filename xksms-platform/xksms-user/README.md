# xksms-user 用户中心

`xksms-user` 提供企业级 SaaS 平台的账号、资料与权限管理能力，并作为认证中心 (`xksms-auth`) 唯一可信的用户数据源。

## 模块划分

```
xksms-platform/xksms-user
├── xksms-user-api   # 对外开放的契约层（DTO、Feign 接口、常量）
└── xksms-user-biz   # 业务实现层（Controller、Domain、Repository）
```

* **xksms-user-api**：暴露最小化的 DTO（如 `UserAuthDTO`、`UserProfileDTO`）以及内部使用的 Feign 接口 `UserFeignClient`，供认证中心和其它服务以编译期契约的方式访问用户能力。
* **xksms-user-biz**：实现多租户账号模型、聚合根与仓储，提供 `/internal/tenants/{tenantId}/users/{username}` 等内部接口。

## 核心特性

1. **多租户隔离**：通过 `TenantId` 值对象和按租户划分的仓储结构确保账号天然按租户分区。
2. **领域拆分**：将账号、资料、权限拆成领域对象 `UserAccount`、`UserProfile` 与 `UserAggregate`，便于落地 DDD 层次化设计。
3. **契约优先**：认证中心仅依赖 `xksms-user-api`，通过 `Result<UserAuthDTO>` 结构消费用户数据，避免对业务实现的侵入。
4. **安全加固**：用户密码统一在加载阶段进行 `{bcrypt}` 编码处理；租户禁用时仓储直接拒绝返回账号，保证安全控制粒度。

## 配置示例

`xksms-user-biz/src/main/resources/application.yml` 提供了多租户引导数据，可直接运行 `UserApplication` 启动服务，并与 `xksms-auth` 联调。

```yaml
xksms:
  user:
    tenants:
      - id: public
        name: 公共云默认租户
        accounts:
          - user-id: 1
            username: admin
            password: Admin@123
            authorities:
              - ROLE_PLATFORM_ADMIN
              - message.read
              - message.write
```

> 生产环境推荐将账号数据落地到数据库，并通过 `UserAccountRepository` 的其它实现（如 MyBatis、JPA）替换当前的内存实现。
