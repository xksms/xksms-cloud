package com.xksms.auth.infrastructure.user;

import com.xksms.auth.domain.service.TenantUserAccountService;
import com.xksms.auth.domain.tenant.TenantId;
import com.xksms.auth.domain.user.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 组合远程和本地实现，保证认证服务的高可用性。
 */
@Component
@Primary
public class CompositeTenantUserAccountService implements TenantUserAccountService {

    private static final Logger log = LoggerFactory.getLogger(CompositeTenantUserAccountService.class);

    private final RemoteTenantUserAccountService remoteService;
    private final InMemoryTenantUserAccountService localService;

    public CompositeTenantUserAccountService(RemoteTenantUserAccountService remoteService,
            InMemoryTenantUserAccountService localService) {
        this.remoteService = remoteService;
        this.localService = localService;
    }

    @Override
    public UserAccount loadUserAccount(TenantId tenantId, String username) {
        if (remoteService != null) {
            try {
                return remoteService.loadUserAccount(tenantId, username);
            } catch (UsernameNotFoundException ex) {
                log.debug("Remote user center reported user [{}] not found in tenant [{}]", username, tenantId.value());
            } catch (Exception ex) {
                log.warn("Remote user center call failed, fallback to local store. cause={}", ex.getMessage());
            }
        }
        if (localService != null) {
            return localService.loadUserAccount(tenantId, username);
        }
        throw new UsernameNotFoundException("User %s not found in tenant %s".formatted(username, tenantId.value()));
    }
}
