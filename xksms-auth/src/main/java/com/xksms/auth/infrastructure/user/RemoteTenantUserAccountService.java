package com.xksms.auth.infrastructure.user;

import com.xksms.auth.domain.service.TenantUserAccountService;
import com.xksms.auth.domain.tenant.TenantId;
import com.xksms.auth.domain.user.UserAccount;
import com.xksms.common.core.Result;
import com.xksms.user.api.dto.UserAuthDTO;
import com.xksms.user.api.feign.UserFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 通过用户中心远程查询账户。
 */
@Component
public class RemoteTenantUserAccountService implements TenantUserAccountService {

    private static final Logger log = LoggerFactory.getLogger(RemoteTenantUserAccountService.class);

    private final UserFeignClient userFeignClient;

    public RemoteTenantUserAccountService(UserFeignClient userFeignClient) {
        this.userFeignClient = userFeignClient;
    }

    @Override
    public UserAccount loadUserAccount(TenantId tenantId, String username) {
        Result<UserAuthDTO> result = userFeignClient.getUserDetailsByUsername(tenantId.value(), username);
        if (result == null) {
            throw new UsernameNotFoundException(
                    "User center response is null for username: " + username + " tenant: " + tenantId.value());
        }
        if (!result.isSuccess()) {
            log.warn("Failed to load user [{}] for tenant [{}] from user center, code={}, message={}", username,
                    tenantId.value(), result.getCode(), result.getMessage());
            throw new UsernameNotFoundException("User center rejected request for tenant %s: %s".formatted(
                    tenantId.value(), result.getMessage()));
        }
        UserAuthDTO dto = result.getData();
        if (dto == null) {
            throw new UsernameNotFoundException(
                    "User center returned empty payload for: " + username + " tenant: " + tenantId.value());
        }
        UserAccount.Builder builder = UserAccount.builder()
                .userId(dto.getUserId())
                .tenantId(tenantId)
                .username(dto.getUsername())
                .password(dto.getPassword())
                .enabled(dto.isEnabled());
        if (!CollectionUtils.isEmpty(dto.getAuthorities())) {
            builder.authorities(dto.getAuthorities());
        }
        return builder.build();
    }
}
