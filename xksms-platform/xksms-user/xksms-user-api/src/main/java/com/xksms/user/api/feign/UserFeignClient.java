package com.xksms.user.api.feign;

import com.xksms.common.core.Result;
import com.xksms.user.api.dto.UserAuthDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户中心内部鉴权相关的 Feign 客户端契约。
 */
@FeignClient(value = "xksms-user", contextId = "userAuthClient", path = "/internal/tenants")
public interface UserFeignClient {

    /**
     * 根据租户和用户名查询认证信息。
     *
     * @param tenantId 租户标识
     * @param username 登录用户名
     * @return 统一响应结构，成功时 data 为 {@link UserAuthDTO}
     */
    @GetMapping("/{tenantId}/users/{username}")
    Result<UserAuthDTO> getUserDetailsByUsername(@PathVariable("tenantId") String tenantId,
            @PathVariable("username") String username);
}
