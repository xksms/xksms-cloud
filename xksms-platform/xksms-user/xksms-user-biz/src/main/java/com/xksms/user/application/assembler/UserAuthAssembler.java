package com.xksms.user.application.assembler;

import com.xksms.user.api.dto.UserAuthDTO;
import com.xksms.user.api.dto.UserProfileDTO;
import com.xksms.user.domain.model.UserAccount;
import com.xksms.user.domain.model.UserAggregate;
import com.xksms.user.domain.model.UserProfile;

import java.util.ArrayList;
import java.util.List;

/**
 * 将领域模型转换为 API DTO。
 */
public final class UserAuthAssembler {

    private UserAuthAssembler() {
    }

    public static UserAuthDTO toDto(UserAggregate aggregate) {
        UserAccount account = aggregate.getAccount();
        UserProfile profile = aggregate.getProfile();
        UserProfileDTO profileDTO = profile == null ? null : UserProfileDTO.builder()
                .displayName(profile.getDisplayName())
                .email(profile.getEmail())
                .mobile(profile.getMobile())
                .avatar(profile.getAvatar())
                .build();
        List<String> authorities = new ArrayList<>(aggregate.getAuthorities());
        return UserAuthDTO.builder()
                .userId(account.getUserId())
                .tenantId(account.getTenantId().value())
                .username(account.getUsername())
                .password(account.getPassword())
                .enabled(account.isEnabled())
                .accountLocked(account.isAccountLocked())
                .accountExpired(account.isAccountExpired())
                .credentialsExpired(account.isCredentialsExpired())
                .authorities(authorities)
                .profile(profileDTO)
                .build();
    }
}
