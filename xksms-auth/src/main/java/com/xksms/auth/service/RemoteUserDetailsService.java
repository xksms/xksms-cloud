package com.xksms.auth.service;

import com.xksms.common.core.Result;
import com.xksms.common.enums.GlobalErrorCodeEnum;
import com.xksms.user.api.dto.UserAuthDTO;
import com.xksms.user.api.feign.UserFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteUserDetailsService implements UserDetailsService {

    private final UserFeignClient userFeignClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!StringUtils.hasText(username)) {
            throw new UsernameNotFoundException("用户名不能为空");
        }

        Result<UserAuthDTO> result;
        try {
            result = userFeignClient.getUserDetailsByUsername(username);
        } catch (Exception ex) {
            log.error("[Auth] 调用用户服务查询用户信息失败, username={}", username, ex);
            throw new UsernameNotFoundException("用户服务暂不可用", ex);
        }

        if (result == null) {
            log.warn("[Auth] 用户服务返回空响应, username={}", username);
            throw new UsernameNotFoundException("用户不存在");
        }

        if (!Objects.equals(result.code(), GlobalErrorCodeEnum.SUCCESS.getCode())) {
            log.warn("[Auth] 用户服务返回非成功状态, username={}, code={}, message={}",
                    username, result.code(), result.message());
            throw new UsernameNotFoundException(result.message());
        }

        UserAuthDTO userAuth = result.data();
        if (userAuth == null) {
            log.warn("[Auth] 用户服务未返回用户数据, username={}", username);
            throw new UsernameNotFoundException("用户不存在");
        }

        String password = userAuth.getPassword();
        if (!StringUtils.hasText(password)) {
            log.warn("[Auth] 用户 {} 未配置密码信息", username);
            throw new UsernameNotFoundException("用户密码信息缺失");
        }

        String[] authorities = CollectionUtils.isEmpty(userAuth.getAuthorities())
                ? new String[0]
                : userAuth.getAuthorities().stream()
                .filter(StringUtils::hasText)
                .toArray(String[]::new);

        return User.builder()
                .username(userAuth.getUsername())
                .password(password)
                .authorities(authorities)
                .disabled(!userAuth.isEnabled())
                .accountExpired(false)
                .credentialsExpired(false)
                .accountLocked(false)
                .build();
    }
}
