package com.xksms.auth.config;

import com.xksms.auth.service.RemoteUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// File: xksms-auth/src/main/java/com/xksms/auth/config/SecurityConfig.java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final RemoteUserDetailsService remoteUserDetailsService;

	@Bean
	@Order(2)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(authorize -> authorize
						.anyRequest().authenticated()
				)
				.formLogin(Customizer.withDefaults()); // 启用表单登录

		// 关键：将我们的 UserDetailsService 配置进去
		http.userDetailsService(remoteUserDetailsService);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		// 必须提供一个密码编码器
		return new BCryptPasswordEncoder();
	}
}