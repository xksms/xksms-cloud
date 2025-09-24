package com.xksms.security.spi;

import com.xksms.log.spi.LogUserProvider;
import com.xksms.security.context.LoginUserContext;

public class SecurityLogUserProvider implements LogUserProvider {
	@Override
	public String getCurrentUserId() {
		return LoginUserContext.getUserId();
	}
}
