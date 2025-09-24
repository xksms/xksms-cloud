package com.xksms.user.infrastructure.repository;

import com.xksms.user.config.UserServiceProperties;
import com.xksms.user.domain.model.TenantId;
import com.xksms.user.domain.model.UserAccount;
import com.xksms.user.domain.model.UserAggregate;
import com.xksms.user.domain.model.UserProfile;
import com.xksms.user.domain.repository.UserAccountRepository;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 基于配置的内存账户仓储，实现租户隔离。
 */
@Repository
public class InMemoryUserAccountRepository implements UserAccountRepository {

    private final Map<String, Boolean> tenantEnabled = new ConcurrentHashMap<>();
    private final Map<String, Map<String, UserAggregate>> usersByTenant = new ConcurrentHashMap<>();

    public InMemoryUserAccountRepository(UserServiceProperties properties) {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        Collection<UserServiceProperties.TenantProperties> tenants = properties.getTenants();
        for (UserServiceProperties.TenantProperties tenant : tenants) {
            String tenantId = TenantId.of(tenant.getId()).value();
            tenantEnabled.put(tenantId, tenant.isEnabled());
            Map<String, UserAggregate> tenantUsers = new ConcurrentHashMap<>();
            if (!CollectionUtils.isEmpty(tenant.getAccounts())) {
                for (UserServiceProperties.AccountProperties account : tenant.getAccounts()) {
                    if (!StringUtils.hasText(account.getUsername()) || !StringUtils.hasText(account.getPassword())) {
                        continue;
                    }
                    String normalizedUsername = account.getUsername().trim();
                    String encodedPassword = encodeIfNecessary(encoder, account.getPassword());
                    UserAccount userAccount = UserAccount.builder()
                            .userId(account.getUserId())
                            .tenantId(TenantId.of(tenantId))
                            .username(normalizedUsername)
                            .password(encodedPassword)
                            .enabled(account.isEnabled())
                            .accountLocked(account.isAccountLocked())
                            .accountExpired(account.isAccountExpired())
                            .credentialsExpired(account.isCredentialsExpired())
                            .build();
                    UserServiceProperties.ProfileProperties profileProps = account.getProfile();
                    UserProfile profile = profileProps == null ? null : UserProfile.builder()
                            .displayName(profileProps.getDisplayName())
                            .email(profileProps.getEmail())
                            .mobile(profileProps.getMobile())
                            .avatar(profileProps.getAvatar())
                            .build();
                    Set<String> authorities = toAuthoritySet(account.getAuthorities());
                    tenantUsers.put(normalizedUsername, new UserAggregate(userAccount, profile, authorities));
                }
            }
            usersByTenant.put(tenantId, tenantUsers);
        }
    }

    @Override
    public Optional<UserAggregate> findByTenantAndUsername(TenantId tenantId, String username) {
        if (!StringUtils.hasText(username)) {
            return Optional.empty();
        }
        String tenantKey = tenantId.value();
        if (!Boolean.TRUE.equals(tenantEnabled.getOrDefault(tenantKey, Boolean.FALSE))) {
            return Optional.empty();
        }
        Map<String, UserAggregate> users = usersByTenant.getOrDefault(tenantKey, Collections.emptyMap());
        return Optional.ofNullable(users.get(username.trim()));
    }

    private static String encodeIfNecessary(PasswordEncoder encoder, String rawPassword) {
        if (!StringUtils.hasText(rawPassword)) {
            return rawPassword;
        }
        String trimmed = rawPassword.trim();
        if (trimmed.startsWith("{")) {
            return trimmed;
        }
        return encoder.encode(trimmed);
    }

    private static Set<String> toAuthoritySet(Collection<String> authorities) {
        if (CollectionUtils.isEmpty(authorities)) {
            return Collections.emptySet();
        }
        return authorities.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
