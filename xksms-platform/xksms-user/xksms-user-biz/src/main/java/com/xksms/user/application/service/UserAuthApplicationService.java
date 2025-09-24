package com.xksms.user.application.service;

import com.xksms.user.api.dto.UserAuthDTO;
import com.xksms.user.application.assembler.UserAuthAssembler;
import com.xksms.user.domain.model.TenantId;
import com.xksms.user.domain.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 认证数据查询应用服务。
 */
@Service
public class UserAuthApplicationService {

    private static final Logger log = LoggerFactory.getLogger(UserAuthApplicationService.class);

    private final UserAccountRepository userAccountRepository;

    public UserAuthApplicationService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public Optional<UserAuthDTO> loadAuthDetails(String tenantId, String username) {
        if (!StringUtils.hasText(username)) {
            return Optional.empty();
        }
        TenantId resolvedTenant = TenantId.of(tenantId);
        return userAccountRepository.findByTenantAndUsername(resolvedTenant, username.trim())
                .map(userAggregate -> {
                    log.debug("Loaded auth details for user [{}] in tenant [{}]", username, resolvedTenant.value());
                    return UserAuthAssembler.toDto(userAggregate);
                });
    }
}
