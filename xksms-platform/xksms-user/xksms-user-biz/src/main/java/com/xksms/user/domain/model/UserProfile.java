package com.xksms.user.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

/**
 * 用户基础资料。
 */
@Getter
@Builder
public final class UserProfile implements Serializable {

    private final String displayName;
    private final String email;
    private final String mobile;
    private final String avatar;
}
