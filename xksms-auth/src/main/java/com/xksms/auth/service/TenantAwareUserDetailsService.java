package com.xksms.auth.service;

import com.xksms.auth.domain.service.TenantUserAccountService;
import com.xksms.auth.domain.tenant.TenantId;
import com.xksms.auth.domain.user.UserAccount;
import com.xksms.auth.infrastructure.tenant.TenantContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 将多租户域模型转换为 Spring Security UserDetails。
 */
@Service
public class TenantAwareUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(TenantAwareUserDetailsService.class);

    private final TenantUserAccountService tenantUserAccountService;

    public TenantAwareUserDetailsService(TenantUserAccountService tenantUserAccountService) {
        this.tenantUserAccountService = tenantUserAccountService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TenantId tenantId = TenantContextHolder.getTenantId();
        log.debug("Loading user [{}] for tenant [{}]", username, tenantId.value());
        UserAccount account = tenantUserAccountService.loadUserAccount(tenantId, username);
        Set<SimpleGrantedAuthority> authorities = account.getAuthorities().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return User.withUsername(account.getUsername())
                .password(account.getPassword())
                .authorities(authorities)
                .disabled(!account.isEnabled())
                .accountExpired(account.isAccountExpired())
                .credentialsExpired(account.isCredentialsExpired())
                .accountLocked(account.isAccountLocked())
                .build();
    }
}
