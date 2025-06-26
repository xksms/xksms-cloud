package com.xksms.security.jwt;

import com.xksms.security.context.LoginUser;
import com.xksms.security.context.LoginUserContext;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

public class JwtAuthenticationFilter implements Filter {

	private final JwtTokenParser tokenParser;

	public JwtAuthenticationFilter(JwtTokenParser tokenParser) {
		this.tokenParser = tokenParser;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			HttpServletRequest http = (HttpServletRequest) request;
			String token = http.getHeader("Authorization");
			if (token != null && token.startsWith("Bearer ")) {
				LoginUser user = tokenParser.parse(token.substring(7));
				LoginUserContext.set(user);
			}
			chain.doFilter(request, response);
		} finally {
			LoginUserContext.clear();
		}
	}
}
