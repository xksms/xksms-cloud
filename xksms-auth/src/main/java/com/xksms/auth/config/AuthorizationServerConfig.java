package com.xksms.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.xksms.auth.infrastructure.tenant.filter.TenantContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

        private final TenantContextFilter tenantContextFilter;
        private final AuthServerProperties properties;

        public AuthorizationServerConfig(TenantContextFilter tenantContextFilter, AuthServerProperties properties) {
                this.tenantContextFilter = tenantContextFilter;
                this.properties = properties;
        }

        @Bean
        @Order(1)
        public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
                OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer<>();
                http.apply(authorizationServerConfigurer);
                authorizationServerConfigurer.oidc(Customizer.withDefaults());

                http.addFilterBefore(tenantContextFilter, UsernamePasswordAuthenticationFilter.class);

                http.exceptionHandling(exceptions -> exceptions
                                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
                                .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));

                return http.build();
        }

        @Bean
        public JWKSource<SecurityContext> jwkSource() {
                KeyPair keyPair = generateRsaKey();
                RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
                RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
                RSAKey rsaKey = new RSAKey.Builder(publicKey)
                                .privateKey(privateKey)
                                .keyID(UUID.randomUUID().toString())
                                .build();
                JWKSet jwkSet = new JWKSet(rsaKey);
                return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
        }

        private static KeyPair generateRsaKey() {
                try {
                        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                        keyPairGenerator.initialize(2048);
                        return keyPairGenerator.generateKeyPair();
                } catch (Exception ex) {
                        throw new IllegalStateException(ex);
                }
        }

        @Bean
        public AuthorizationServerSettings authorizationServerSettings() {
                AuthorizationServerSettings.Builder builder = AuthorizationServerSettings.builder();
                if (StringUtils.hasText(properties.getIssuer())) {
                        builder.issuer(properties.getIssuer());
                }
                return builder.build();
        }
}
