package com.xksms.auth.domain.service;

import com.xksms.auth.domain.tenant.TenantId;
import com.xksms.auth.domain.user.UserAccount;

/**
 * 租户维度的用户账户查询服务。
 */
public interface TenantUserAccountService {

    /**
     * 根据租户与用户名获取账户信息。
     *
     * @param tenantId 当前租户
     * @param username 用户名
     * @return 用户账户
     */
    UserAccount loadUserAccount(TenantId tenantId, String username);
}
