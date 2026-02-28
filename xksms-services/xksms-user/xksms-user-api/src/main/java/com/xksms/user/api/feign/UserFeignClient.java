// File: xksms-user-api/src/main/java/com/xksms/user/api/feign/UserFeignClient.java
package com.xksms.user.api.feign;

import com.xksms.common.core.Result;
import com.xksms.user.api.dto.UserAuthDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务 Feign 客户端接口。
 * [name]: 必须与用户服务的 spring.application.name 一致。
 * [path]: 定义一个统一的 API 前缀。
 */
@FeignClient(name = "xksms-user", path = "/users")
public interface UserFeignClient {

    /**
     * 根据用户名查询用户信息（供内部认证使用）。
     *
     * @param username 用户名
     * @return 包含用户认证信息的 Result 对象
     */
    @GetMapping("/internal/details/{username}")
    Result<UserAuthDTO> getUserDetailsByUsername(@PathVariable("username") String username);
}