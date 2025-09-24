package com.xksms.auth.domain.tenant;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * 企业级 SaaS 租户描述。
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class Tenant implements Serializable {

    private final TenantId id;
    private final String name;
    private final boolean enabled;

    public static Tenant of(TenantId id, String name, boolean enabled) {
        return new Tenant(Objects.requireNonNull(id, "id"),
                Objects.requireNonNullElse(name, id.value()),
                enabled);
    }
}
