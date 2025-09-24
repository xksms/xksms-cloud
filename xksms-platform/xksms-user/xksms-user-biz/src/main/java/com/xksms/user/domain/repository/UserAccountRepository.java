package com.xksms.user.domain.repository;

import com.xksms.user.domain.model.TenantId;
import com.xksms.user.domain.model.UserAggregate;

import java.util.Optional;

/**
 * 用户账户读取仓储。
 */
public interface UserAccountRepository {

    Optional<UserAggregate> findByTenantAndUsername(TenantId tenantId, String username);
}
