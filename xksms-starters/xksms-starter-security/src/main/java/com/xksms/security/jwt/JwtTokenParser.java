package com.xksms.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xksms.security.context.LoginUser;

public class JwtTokenParser {

	public LoginUser parse(String token) {
		DecodedJWT jwt = JWT.decode(token);
		String userId = jwt.getSubject();
		String username = jwt.getClaim("username").asString();

		LoginUser user = new LoginUser();
		user.setUserId(userId);
		user.setUsername(username);
		return user;
	}
}
