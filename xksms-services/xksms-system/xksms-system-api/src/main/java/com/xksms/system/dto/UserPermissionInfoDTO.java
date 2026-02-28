package com.xksms.system.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 内部认证使用的、用户在特定租户下的权限信息
 */
@Data
public class UserPermissionInfoDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户的角色编码集合 (e.g., "ROLE_ADMIN", "ROLE_OPERATOR")
	 */
	private Set<String> roles;

	/**
	 * 用户的权限标识集合 (e.g., "user:create", "order:read")
	 */
	private Set<String> permissions;
}