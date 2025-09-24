package com.xksms.auth.infrastructure.oauth2;

import com.xksms.auth.config.AuthServerProperties;
import com.xksms.auth.domain.tenant.TenantId;
import com.xksms.auth.infrastructure.tenant.TenantContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多租户感知的内存客户端仓储。
 */
@Component
public class InMemoryTenantRegisteredClientRepository implements RegisteredClientRepository {

    private final PasswordEncoder passwordEncoder;
    private final Map<String, Map<String, RegisteredClient>> clientsByTenant = new ConcurrentHashMap<>();
    private final Map<String, RegisteredClient> clientsById = new ConcurrentHashMap<>();

    public InMemoryTenantRegisteredClientRepository(AuthServerProperties properties, PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        properties.getTenants().forEach(tenant -> {
            TenantId tenantId = TenantId.of(tenant.getId());
            Map<String, RegisteredClient> tenantClients = new ConcurrentHashMap<>();
            tenant.getClients().forEach(clientProperties -> {
                RegisteredClient registeredClient = buildRegisteredClient(tenantId, clientProperties);
                tenantClients.put(registeredClient.getClientId(), registeredClient);
                clientsById.put(registeredClient.getId(), registeredClient);
            });
            clientsByTenant.put(tenantId.value(), tenantClients);
        });
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        TenantId tenantId = TenantContextHolder.getTenantId();
        clientsByTenant.computeIfAbsent(tenantId.value(), key -> new ConcurrentHashMap<>())
                .put(registeredClient.getClientId(), registeredClient);
        clientsById.put(registeredClient.getId(), registeredClient);
    }

    @Override
    public RegisteredClient findById(String id) {
        return clientsById.get(id);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        TenantId tenantId = TenantContextHolder.getTenantId();
        Map<String, RegisteredClient> clients = clientsByTenant.get(tenantId.value());
        if (clients != null) {
            RegisteredClient registeredClient = clients.get(clientId);
            if (registeredClient != null) {
                return registeredClient;
            }
        }
        return clientsByTenant.values().stream()
                .map(map -> map.get(clientId))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    private RegisteredClient buildRegisteredClient(TenantId tenantId, AuthServerProperties.ClientProperties client) {
        String internalId = StringUtils.hasText(client.getId()) ? client.getId() : UUID.randomUUID().toString();
        RegisteredClient.Builder builder = RegisteredClient.withId(internalId)
                .clientId(client.getClientId());

        if (!client.getGrantTypes().isEmpty()) {
            client.getGrantTypes().forEach(grantType -> builder.authorizationGrantType(new AuthorizationGrantType(grantType)));
        } else {
            builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN);
        }

        if (!client.getScopes().isEmpty()) {
            client.getScopes().forEach(builder::scope);
        }

        if (!client.getRedirectUris().isEmpty()) {
            client.getRedirectUris().forEach(builder::redirectUri);
        }

        if (!client.getPostLogoutRedirectUris().isEmpty()) {
            client.getPostLogoutRedirectUris().forEach(builder::postLogoutRedirectUri);
        }

        if (StringUtils.hasText(client.getClientSecret())) {
            builder.clientSecret(encodeIfNecessary(client.getClientSecret()))
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST);
        } else {
            builder.clientAuthenticationMethod(ClientAuthenticationMethod.NONE);
        }

        ClientSettings clientSettings = ClientSettings.builder()
                .requireProofKey(client.isRequireProofKey())
                .requireAuthorizationConsent(client.isRequireAuthorizationConsent())
                .build();

        AuthServerProperties.TokenSettingsProperties token = client.getToken();
        TokenSettings defaultSettings = TokenSettings.builder().build();
        TokenSettings tokenSettings = TokenSettings.builder()
                .accessTokenTimeToLive(Optional.ofNullable(token)
                        .map(AuthServerProperties.TokenSettingsProperties::getAccessTokenTtl)
                        .orElse(defaultSettings.getAccessTokenTimeToLive()))
                .refreshTokenTimeToLive(Optional.ofNullable(token)
                        .map(AuthServerProperties.TokenSettingsProperties::getRefreshTokenTtl)
                        .orElse(defaultSettings.getRefreshTokenTimeToLive()))
                .idTokenTimeToLive(Optional.ofNullable(token)
                        .map(AuthServerProperties.TokenSettingsProperties::getIdTokenTtl)
                        .orElse(defaultSettings.getIdTokenTimeToLive()))
                .build();

        builder.clientSettings(clientSettings)
                .tokenSettings(tokenSettings)
                .clientIdIssuedAt(java.time.Instant.now());

        // 使用租户标识作为 scope 前缀，确保跨租户隔离
        builder.scope("tenant:" + tenantId.value());

        return builder.build();
    }

    private String encodeIfNecessary(String secret) {
        if (!StringUtils.hasText(secret)) {
            return secret;
        }
        if (secret.startsWith("{")) {
            return secret;
        }
        return passwordEncoder.encode(secret);
    }
}
