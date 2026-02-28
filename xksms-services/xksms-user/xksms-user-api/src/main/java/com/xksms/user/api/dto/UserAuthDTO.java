package com.xksms.user.api.dto;

import lombok.Data;
import java.io.Serializable;
import java.util.Set;

/**
 * 内部认证使用的用户数据传输对象。
 * 仅用于 xksms-user 和 xksms-auth 服务之间的通信。
 */
@Data
public class UserAuthDTO implements Serializable {
	private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    /**
     * 加密后的密码
     */
    private String password;
    private boolean enabled;
    /**
     * 用户的权限标识集合
     */
    private Set<String> authorities;
}