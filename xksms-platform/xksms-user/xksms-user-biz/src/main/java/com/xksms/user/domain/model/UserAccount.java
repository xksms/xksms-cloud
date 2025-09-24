package com.xksms.user.domain.model;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

/**
 * 认证所需的账户核心信息。
 */
@Getter
public final class UserAccount implements Serializable {

    private final Long userId;
    private final TenantId tenantId;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final boolean accountLocked;
    private final boolean accountExpired;
    private final boolean credentialsExpired;

    private UserAccount(Builder builder) {
        this.userId = builder.userId;
        this.tenantId = Objects.requireNonNull(builder.tenantId, "tenantId");
        this.username = Objects.requireNonNull(builder.username, "username");
        this.password = Objects.requireNonNull(builder.password, "password");
        this.enabled = builder.enabled;
        this.accountLocked = builder.accountLocked;
        this.accountExpired = builder.accountExpired;
        this.credentialsExpired = builder.credentialsExpired;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long userId;
        private TenantId tenantId = TenantId.of(null);
        private String username;
        private String password;
        private boolean enabled = true;
        private boolean accountLocked;
        private boolean accountExpired;
        private boolean credentialsExpired;

        private Builder() {
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder tenantId(TenantId tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder accountLocked(boolean accountLocked) {
            this.accountLocked = accountLocked;
            return this;
        }

        public Builder accountExpired(boolean accountExpired) {
            this.accountExpired = accountExpired;
            return this;
        }

        public Builder credentialsExpired(boolean credentialsExpired) {
            this.credentialsExpired = credentialsExpired;
            return this;
        }

        public UserAccount build() {
            return new UserAccount(this);
        }
    }
}
