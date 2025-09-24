package com.xksms.auth.infrastructure.tenant.feign;

import com.xksms.auth.infrastructure.tenant.TenantContextHolder;
import com.xksms.auth.infrastructure.tenant.filter.TenantContextFilter;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

/**
 * 将租户信息透传给下游服务。
 */
@Component
public class TenantFeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        var tenantId = TenantContextHolder.getTenantId();
        if (tenantId != null) {
            template.header(TenantContextFilter.TENANT_HEADER, tenantId.value());
        }
    }
}
