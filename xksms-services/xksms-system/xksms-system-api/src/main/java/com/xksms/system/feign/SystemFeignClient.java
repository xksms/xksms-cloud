// File: xksms-system-api/src/main/java/com/xksms/system/api/feign/SystemFeignClient.java
package com.xksms.system.feign;

import com.xksms.common.core.Result;
import com.xksms.system.dto.UserPermissionInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "xksms-system", path = "/system")
public interface SystemFeignClient {

	/**
	 * [内部接口] 获取用户在指定租户下的角色和权限信息
	 * @param userId    用户ID
	 * @param tenantId  租户ID
	 * @return 用户的权限信息
	 */
	@GetMapping("/internal/permissions")
	Result<UserPermissionInfoDTO> getUserPermissionInfo(@RequestParam("userId") Long userId, @RequestParam("tenantId") Long tenantId);
}