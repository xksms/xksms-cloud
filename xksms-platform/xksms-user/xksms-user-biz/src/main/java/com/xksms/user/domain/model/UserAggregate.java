package com.xksms.user.domain.model;

import lombok.Getter;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 用户账户聚合，组合账户、资料与权限。
 */
@Getter
public final class UserAggregate implements Serializable {

    private final UserAccount account;
    private final UserProfile profile;
    private final Set<String> authorities;

    public UserAggregate(UserAccount account, UserProfile profile, Set<String> authorities) {
        this.account = Objects.requireNonNull(account, "account");
        this.profile = profile;
        this.authorities = Collections.unmodifiableSet(new LinkedHashSet<>(Objects.requireNonNull(authorities, "authorities")));
    }
}
