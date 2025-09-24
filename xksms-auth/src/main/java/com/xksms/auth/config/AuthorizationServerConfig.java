package com.xksms.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

	// 关键点3：这是处理 OAuth2.0 授权服务的过滤器链
	@Bean
	@Order(1)
	public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		// [旧的、废弃的方式]
		// OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

		// [新的、推荐的方式]
		// 1. 创建并应用 OAuth2AuthorizationServerConfigurer
		//    这就等同于将授权服务器的所有端点和逻辑应用到这个 HttpSecurity 实例上。
		http.apply(new OAuth2AuthorizationServerConfigurer());

		// 2. [可选] 如果需要，可以获取该 configurer 实例进行深度定制
		// OAuth2AuthorizationServerConfigurer configurer = http.getConfigurer(OAuth2AuthorizationServerConfigurer.class);
		// configurer.tokenEndpoint(endpoint -> ...);

		// 3. 继续我们其他的配置
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
				.oidc(oidc -> {
				}); // 启用 OIDC 的方式不变，它本身就是对 configurer 的操作

		http
				.exceptionHandling(exceptions -> exceptions
						.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
				)
				.oauth2ResourceServer(resourceServer -> resourceServer
						.jwt(jwt -> {
						})
				);

		return http.build();
	}

	// 关键点4：注册一个客户端，后续也应替换为数据库存储
	@Bean
	public RegisteredClientRepository registeredClientRepository() {
		RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId("messaging-client")
				.clientSecret("{noop}secret")
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
				.redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
				.redirectUri("http://127.0.0.1:8080/authorized")
				.scope(OidcScopes.OPENID)
				.scope(OidcScopes.PROFILE)
				.scope("message.read")
				.scope("message.write")
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
				.build();
		return new InMemoryRegisteredClientRepository(registeredClient);
	}

	// 关键点5：配置 JWT 签名的密钥对
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
		KeyPair keyPair;
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPair = keyPairGenerator.generateKeyPair();
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		return keyPair;
	}

	// 关键点6：配置授权服务器的元信息
	@Bean
	public AuthorizationServerSettings authorizationServerSettings() {
		return AuthorizationServerSettings.builder().build();
	}
}