package com.xksms.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 认证服务器的多租户配置。
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "xksms.auth")
public class AuthServerProperties {

    private String defaultTenant = "public";
    private List<TenantProperties> tenants = new ArrayList<>();
    private String issuer;

    @Getter
    @Setter
    public static class TenantProperties {
        private String id;
        private String name;
        private boolean enabled = true;
        private List<ClientProperties> clients = new ArrayList<>();
        private List<UserProperties> bootstrapUsers = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class ClientProperties {
        private String id;
        private String clientId;
        private String clientSecret;
        private Set<String> grantTypes = new HashSet<>();
        private Set<String> scopes = new HashSet<>();
        private Set<String> redirectUris = new HashSet<>();
        private Set<String> postLogoutRedirectUris = new HashSet<>();
        private boolean requireProofKey;
        private boolean requireAuthorizationConsent = true;
        private TokenSettingsProperties token = new TokenSettingsProperties();
    }

    @Getter
    @Setter
    public static class TokenSettingsProperties {
        private Duration accessTokenTtl = Duration.ofMinutes(30);
        private Duration refreshTokenTtl = Duration.ofDays(1);
        private Duration idTokenTtl = Duration.ofMinutes(10);
    }

    @Getter
    @Setter
    public static class UserProperties {
        private Long userId;
        private String username;
        private String password;
        private boolean enabled = true;
        private boolean accountLocked;
        private boolean accountExpired;
        private boolean credentialsExpired;
        private Set<String> authorities = new HashSet<>();
    }
}
