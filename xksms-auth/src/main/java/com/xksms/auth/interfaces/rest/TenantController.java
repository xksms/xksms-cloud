package com.xksms.auth.interfaces.rest;

import com.xksms.auth.config.AuthServerProperties;
import com.xksms.auth.infrastructure.tenant.TenantRegistry;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 对外暴露租户元数据的接口，供前端或其他系统发现能力使用。
 */
@RestController
@RequestMapping(path = "/tenants", produces = MediaType.APPLICATION_JSON_VALUE)
public class TenantController {

    private final TenantRegistry tenantRegistry;
    private final AuthServerProperties properties;

    public TenantController(TenantRegistry tenantRegistry, AuthServerProperties properties) {
        this.tenantRegistry = tenantRegistry;
        this.properties = properties;
    }

    @GetMapping
    public List<TenantView> tenants() {
        String defaultTenant = properties.getDefaultTenant();
        return tenantRegistry.getAllTenants().stream()
                .map(tenant -> new TenantView(tenant.getId().value(), tenant.getName(), tenant.isEnabled(),
                        tenant.getId().value().equals(defaultTenant)))
                .collect(Collectors.toList());
    }

    public record TenantView(String id, String name, boolean enabled, boolean defaultTenant) {
    }
}

