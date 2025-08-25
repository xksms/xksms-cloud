# xksms-starter-redis

`xksms-starter-redis` 是 `xksms-cloud` 微服务平台的**核心基础设施组件**。它不仅仅是一个 Redis 客户端的封装，更是整个平台关于 **Redis 使用规范、序列化标准和开发者体验**的基石。

## 🧠 设计哲学 (Design Philosophy)

我们构建此 Starter 的核心目标，是提供一个**“健robust的工具箱”**和**“清晰的标准”**，而非一个大而全的“服务”。

1. **分层的架构**：本 Starter 精心地区分了两个层次的组件：
    * **高阶服务 (`RedisHelper`)**：这是我们为业务开发者铺设的**“康庄大道”**。它封装了繁琐的 API，提供了极致的类型安全和空指针安全，是业务代码中**唯一推荐**使用的 Redis 组件。
    * **低阶工具 (`RedisTemplate`)**：我们承认无法预知所有需求。因此，我们保留了一个底层的 `RedisTemplate` Bean 作为**“逃生舱”**，供开发者在处理极其生僻的 Redis 操作时使用。

2. **约定优于配置**：我们坚信“Key 为 String，Value 为 JSON”是微服务场景下的最佳序列化实践。本 Starter 将此实践固化为**默认约定**，通过提供一个统一的 `RedisSerializer<Object>` Bean，确保了整个平台数据格式的一致性。

3. **开发者体验优先**：通过提供 `redis-default.yml`，我们实现了真正的“开箱即用”。同时，启动时的连接校验 `RedisConnectionVerifier` 为开发者提供了即时、明确的反馈。

## ✨ 核心功能 (Core Features)

* ✅ **提供高阶服务 `RedisHelper`**：封装常用操作，返回 `Optional<T>`，业务代码零 `null` 判断，零不安全类型转换。
* ✅ **统一的 JSON 序列化**：默认提供一个全局唯一的 `RedisSerializer<Object>` Bean，所有数据以可读、可移植的 JSON 格式存储。
* ✅ **完整的连接池配置**：通过 `xksms.redis.lettuce.pool.*` 提供对 Lettuce 连接池的全面配置能力。
* ✅ **启动时连接校验**：应用启动后自动执行 `PING` 命令，即时发现配置或网络问题。
* ✅ **约定式配置文件**：内置 `redis-default.yml`，简化业务模块的接入成本。

## 🚀 快速开始 (Quick Start)

### 1. 引入依赖

在你的业务模块 `pom.xml` 中引入本 Starter。

```xml

<dependency>
    <groupId>com.xksms</groupId>
    <artifactId>xksms-starter-redis</artifactId>
    <version>${revision}</version>
</dependency>
```

2. 导入默认配置
   在你的 application.yml 中，导入 Starter 提供的默认配置文件。这是推荐的做法。

```YAML
spring:
  config:
    import:
      - optional:classpath:redis-default.yml
```

3. 覆盖环境配置
   根据你的实际环境，覆盖必要的配置项。

```YAML
xksms:
  redis:
    host: 192.168.1.100  # 覆盖默认的 localhost
    port: 6379
    password: "your_password"
    database: 1
```

💡 核心用法 (Core Usage)
推荐方式：注入 RedisHelper
这是为所有业务场景设计的、最安全、最便捷的使用方式。

```Java

import com.xksms.redis.RedisHelper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	private final RedisHelper redisHelper;

	// 通过构造器注入，依赖关系清晰
	public UserServiceImpl(RedisHelper redisHelper) {
		this.redisHelper = redisHelper;
	}

	public Optional<User> findUserById(Long id) {
		String key = "user:" + id;
		// get 方法返回 Optional<User>，业务代码无需处理 null 和类型转换
		return redisHelper.get(key, User.class);
	}
}
```

特殊场景：注入 RedisTemplate
仅当你需要执行 RedisHelper 尚未封装的底层、复杂操作时，才考虑使用此方法。此用法必须在**代码评审 (Code Review)**中进行说明。

```Java

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AdvancedRedisOperations {

	// [注意] 注入的是 RedisTemplate<String, Object> 类型
	// Bean 的名称在我们的 starter 中为 xksmsCoreRedisTemplate
	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	public void performBitOperation() {
		// 例如：执行一个 RedisHelper 没有封装的位图操作
		redisTemplate.opsForValue().setBit("user:online:status", 1024, true);
	}
}
```

# ⚙️ 配置属性详解（Configuration Properties）

**前缀：** `xksms.redis`

| 配置项                       | 描述                        | 默认值         |
|---------------------------|---------------------------|-------------|
| `enabled`                 | 是否启用本 Starter             | `true`      |
| `host`                    | Redis 服务器地址               | `localhost` |
| `port`                    | Redis 服务器端口               | `6379`      |
| `database`                | 数据库索引（0-15）               | `0`         |
| `password`                | Redis 访问密码                | `null`      |
| `timeout`                 | 连接超时时间（毫秒）                | `5000`      |
| `verify-on-startup`       | 是否在启动后校验连接                | `true`      |
| `lettuce.pool.max-active` | 连接池最大连接数                  | `8`         |
| `lettuce.pool.max-idle`   | 连接池最大空闲连接                 | `8`         |
| `lettuce.pool.min-idle`   | 连接池最小空闲连接                 | `0`         |
| `lettuce.pool.max-wait`   | 连接池耗尽时等待时间（毫秒），`-1` 为无限等待 | `-1`        |

---

# 📦 提供的 Beans（Provided Beans）

| Bean 名称                   | Bean 类型                         | 描述                                   |
|---------------------------|---------------------------------|--------------------------------------|
| `redisHelper`             | `RedisHelper`                   | **推荐**：高阶、安全的 Redis 服务助手             |
| `xksmsCoreRedisTemplate`  | `RedisTemplate<String, Object>` | **底层**：配置好序列化的低阶 Redis 模板            |
| `redisValueSerializer`    | `RedisSerializer<Object>`       | 全局统一的、基于 Jackson 的 JSON 序列化器         |
| `redisConnectionFactory`  | `LettuceConnectionFactory`      | 底层的、带连接池的 Lettuce 连接工厂               |
| `redisConnectionVerifier` | `RedisConnectionVerifier`       | 启动时连接校验器（当 `verify-on-startup=true`） |

---

# 📝 版本兼容（Version Compatibility）

- **Java**：21+
- **Spring Boot**：3.2.x+
