# xksms-starter-security

`xksms-starter-security` 是微服务系统的统一认证鉴权 Starter，基于 Spring Security 和 JWT 实现，同时对日志模块提供 `LogUserProvider` SPI 实现，完成用户上下文注入与日志 trace 关联。

---

## ✨ 特性

- ✅ 基于 JWT 实现服务无状态认证机制
- ✅ 自动注入登录用户上下文 `LoginUserContext`
- ✅ 支持日志模块中 SPI 接口 `LogUserProvider` 自动装配
- ✅ 安全模块封装为 starter，方便全局接入与维护

---

## 📦 依赖引入

```xml

<dependency>
    <groupId>com.xksms</groupId>
    <artifactId>xksms-starter-security</artifactId>
</dependency>
```

---

## 🧩 自动配置类

```java
import com.xksms.security.jwt.JwtAuthenticationFilter;
import com.xksms.security.spi.SecurityLogUserProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.SecurityFilterChain;

@AutoConfiguration
@ConditionalOnClass(SecurityFilterChain.class)
public class SecurityAutoConfiguration {

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		xxxxxxx
	}

	@Bean
	public SecurityLogUserProvider securityLogUserProvider() {
		return new SecurityLogUserProvider();
	}
}
```

---

## 🧠 关键类说明

| 类/接口名                     | 说明                             |
|---------------------------|--------------------------------|
| `JwtAuthenticationFilter` | JWT 鉴权过滤器，从 Header 解析并注入用户信息   |
| `JwtTokenParser`          | JWT Token 的解析器                 |
| `LoginUserContext`        | 用户上下文工具类，使用 ThreadLocal 管理登录用户 |
| `LoginUser`               | 登录用户对象模型（userId、租户等）           |
| `SecurityLogUserProvider` | 日志模块的 SPI 实现，提供当前 userId       |

---

## 🧩 SPI 对接日志模块

```java
public class SecurityLogUserProvider implements LogUserProvider {
	@Override
	public String getCurrentUserId() {
		return LoginUserContext.getUserId(); // 从 ThreadLocal 获取
	}
}
```

---

## 🔐 JWT 鉴权工作流

1. 客户端通过登录接口获取 JWT
2. 每次请求在 Header 携带：`Authorization: Bearer xxx`
3. `JwtAuthenticationFilter` 拦截请求并解析 token
4. 解析结果注入 Spring Security 上下文
5. `LoginUserContext` 通过 ThreadLocal 提供用户信息

---

## 📂 目录结构

```
xksms-starter-security
├── config/SecurityAutoConfiguration.java      # 自动配置入口
├── jwt/JwtTokenParser.java                    # JWT 解析器
├── jwt/JwtAuthenticationFilter.java           # JWT 认证过滤器
├── context/LoginUserContext.java              # 用户上下文 ThreadLocal 工具
├── context/LoginUser.java                     # 登录用户对象模型
├── spi/SecurityLogUserProvider.java           # 实现日志 SPI 的用户提供器
└── resources/
    └── META-INF/
        └── spring/
            └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

---

## 🧪 测试建议

- 使用 JWT 工具生成一个有效 token，请求业务接口验证是否能正确注入用户信息
- 结合 `xksms-starter-log` 验证日志中 userId 能否正确输出

---

## 📝 版本说明

- 当前版本：`1.0.0-SNAPSHOT`
- 兼容版本：Spring Boot 3.2.x / Java 17+
