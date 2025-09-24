package com.xksms.user.api.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户基础资料对外传输对象。
 */
@Getter
@Builder
public final class UserProfileDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String displayName;
    private final String email;
    private final String mobile;
    private final String avatar;
}
