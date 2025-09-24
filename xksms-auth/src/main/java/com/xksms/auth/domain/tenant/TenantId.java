package com.xksms.auth.domain.tenant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 租户标识值对象。
 */
@Getter
@ToString
@EqualsAndHashCode
public final class TenantId implements Serializable {

    public static final String DEFAULT_VALUE = "public";

    private final String value;

    private TenantId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Tenant id must not be blank");
        }
        this.value = value;
    }

    public static TenantId of(String value) {
        if (value == null || value.isBlank()) {
            return new TenantId(DEFAULT_VALUE);
        }
        return new TenantId(value.trim());
    }

    public static TenantId defaultTenant() {
        return new TenantId(DEFAULT_VALUE);
    }
}
