package com.xksms.auth.infrastructure.tenant;

import com.xksms.auth.domain.tenant.TenantId;

/**
 * 基于 ThreadLocal 的租户上下文。
 */
public final class TenantContextHolder {

    private static final ThreadLocal<TenantId> CONTEXT = new ThreadLocal<>();

    private TenantContextHolder() {
    }

    public static void setTenantId(TenantId tenantId) {
        if (tenantId == null) {
            CONTEXT.set(TenantId.defaultTenant());
            return;
        }
        CONTEXT.set(tenantId);
    }

    public static TenantId getTenantId() {
        TenantId tenantId = CONTEXT.get();
        return tenantId != null ? tenantId : TenantId.defaultTenant();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
