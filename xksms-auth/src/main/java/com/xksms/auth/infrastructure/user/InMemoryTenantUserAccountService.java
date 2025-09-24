package com.xksms.auth.infrastructure.user;

import com.xksms.auth.config.AuthServerProperties;
import com.xksms.auth.domain.service.TenantUserAccountService;
import com.xksms.auth.domain.tenant.TenantId;
import com.xksms.auth.domain.user.UserAccount;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 读取配置文件中初始化账户的本地实现。
 */
@Component
public class InMemoryTenantUserAccountService implements TenantUserAccountService {

    private final PasswordEncoder passwordEncoder;
    private final Map<String, Map<String, UserAccount>> usersByTenant = new ConcurrentHashMap<>();

    public InMemoryTenantUserAccountService(AuthServerProperties properties, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        properties.getTenants().forEach(tenant -> {
            Map<String, UserAccount> users = new ConcurrentHashMap<>();
            tenant.getBootstrapUsers().forEach(user -> {
                if (!StringUtils.hasText(user.getUsername()) || !StringUtils.hasText(user.getPassword())) {
                    return;
                }
                String encodedPassword = encodeIfNecessary(user.getPassword());
                UserAccount userAccount = UserAccount.builder()
                        .userId(user.getUserId())
                        .tenantId(TenantId.of(tenant.getId()))
                        .username(user.getUsername())
                        .password(encodedPassword)
                        .enabled(user.isEnabled())
                        .accountLocked(user.isAccountLocked())
                        .accountExpired(user.isAccountExpired())
                        .credentialsExpired(user.isCredentialsExpired())
                        .authorities(user.getAuthorities())
                        .build();
                users.put(userAccount.getUsername(), userAccount);
            });
            usersByTenant.put(TenantId.of(tenant.getId()).value(), users);
        });
    }

    @Override
    public UserAccount loadUserAccount(TenantId tenantId, String username) {
        Map<String, UserAccount> users = usersByTenant.get(tenantId.value());
        if (users == null || users.isEmpty()) {
            throw new UsernameNotFoundException("No local user configured for tenant: " + tenantId.value());
        }
        UserAccount userAccount = users.get(username);
        if (userAccount == null) {
            throw new UsernameNotFoundException("User %s not found in tenant %s".formatted(username, tenantId.value()));
        }
        return userAccount;
    }

    private String encodeIfNecessary(String rawPassword) {
        if (!StringUtils.hasText(rawPassword)) {
            return rawPassword;
        }
        if (rawPassword.startsWith("{")) {
            return rawPassword;
        }
        return passwordEncoder.encode(rawPassword);
    }
}
