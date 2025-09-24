package com.xksms.user.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 用户中心内部使用的租户标识值对象。
 */
@Getter
@ToString
@EqualsAndHashCode
public final class TenantId implements Serializable {

    public static final String DEFAULT_TENANT = "public";

    private final String value;

    private TenantId(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("tenant id must not be blank");
        }
        this.value = value;
    }

    public static TenantId of(String value) {
        if (value == null || value.isBlank()) {
            return new TenantId(DEFAULT_TENANT);
        }
        return new TenantId(value.trim());
    }
}
