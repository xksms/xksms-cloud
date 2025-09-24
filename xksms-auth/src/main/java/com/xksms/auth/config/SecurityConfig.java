package com.xksms.auth.config;

import com.xksms.auth.infrastructure.tenant.filter.TenantContextFilter;
import com.xksms.auth.service.TenantAwareUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final TenantAwareUserDetailsService tenantAwareUserDetailsService;
        private final TenantContextFilter tenantContextFilter;

        public SecurityConfig(TenantAwareUserDetailsService tenantAwareUserDetailsService,
                        TenantContextFilter tenantContextFilter) {
                this.tenantAwareUserDetailsService = tenantAwareUserDetailsService;
                this.tenantContextFilter = tenantContextFilter;
        }

        @Bean
        @Order(2)
        public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("/.well-known/**", "/actuator/health", "/actuator/info", "/tenants/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(Customizer.withDefaults())
                                .logout(Customizer.withDefaults())
                                .csrf(csrf -> csrf.ignoringRequestMatchers("/tenants/**"));

                http.addFilterBefore(tenantContextFilter, UsernamePasswordAuthenticationFilter.class);

                // 关键：将我们的 UserDetailsService 配置进去
                http.userDetailsService(tenantAwareUserDetailsService);

                return http.build();
        }

	@Bean
	public PasswordEncoder passwordEncoder() {
		// 必须提供一个密码编码器
		return new BCryptPasswordEncoder();
	}
}
