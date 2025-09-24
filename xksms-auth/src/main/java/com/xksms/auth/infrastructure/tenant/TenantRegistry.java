package com.xksms.auth.infrastructure.tenant;

import com.xksms.auth.config.AuthServerProperties;
import com.xksms.auth.domain.tenant.Tenant;
import com.xksms.auth.domain.tenant.TenantId;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 维护租户配置的注册表。
 */
@Component
public class TenantRegistry {

    private final Map<String, Tenant> tenants = new LinkedHashMap<>();

    public TenantRegistry(AuthServerProperties properties) {
        properties.getTenants().forEach(tenant -> {
            TenantId tenantId = TenantId.of(tenant.getId());
            Tenant domainTenant = Tenant.of(tenantId, tenant.getName(), tenant.isEnabled());
            tenants.put(tenantId.value(), domainTenant);
        });
    }

    public Optional<Tenant> findTenant(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            return Optional.ofNullable(tenants.get(TenantId.DEFAULT_VALUE));
        }
        return Optional.ofNullable(tenants.get(tenantId));
    }

    public Collection<Tenant> getAllTenants() {
        return Collections.unmodifiableCollection(tenants.values());
    }
}
