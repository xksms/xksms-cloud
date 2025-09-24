package com.xksms.user.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务的多租户初始化配置。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "xksms.user")
@Validated
public class UserServiceProperties {

    @Valid
    @NotEmpty(message = "至少需要配置一个租户")
    private List<TenantProperties> tenants = new ArrayList<>();

    /**
     * 租户级别配置。
     */
    @Getter
    @Setter
    public static class TenantProperties {

        @NotBlank(message = "租户ID不能为空")
        private String id;

        @NotBlank(message = "租户名称不能为空")
        private String name;

        private boolean enabled = true;

        @Valid
        @NotEmpty(message = "租户必须配置至少一个账户")
        private List<AccountProperties> accounts = new ArrayList<>();
    }

    /**
     * 用户账户配置。
     */
    @Getter
    @Setter
    public static class AccountProperties {

        private Long userId;

        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        private String password;

        private boolean enabled = true;

        private boolean accountLocked;

        private boolean accountExpired;

        private boolean credentialsExpired;

        @Size(min = 1, message = "至少配置一个权限标识")
        private List<String> authorities = new ArrayList<>();

        @Valid
        private ProfileProperties profile = new ProfileProperties();
    }

    /**
     * 用户资料配置。
     */
    @Getter
    @Setter
    public static class ProfileProperties {
        private String displayName;
        private String email;
        private String mobile;
        private String avatar;
    }
}
