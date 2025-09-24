package com.xksms.auth.config;

import com.xksms.auth.infrastructure.tenant.TenantRegistry;
import com.xksms.auth.infrastructure.tenant.filter.TenantContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 租户基础设施装配。
 */
@Configuration
public class TenantConfiguration {

    @Bean
    public TenantContextFilter tenantContextFilter(TenantRegistry tenantRegistry, AuthServerProperties properties) {
        return new TenantContextFilter(tenantRegistry, properties);
    }
}
