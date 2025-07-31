package com.xksms.security.context;

public class LoginUserContext {

	private static final ThreadLocal<LoginUser> CONTEXT = new ThreadLocal<>();

	public static void set(LoginUser user) {
		CONTEXT.set(user);
	}

	public static LoginUser get() {
		return CONTEXT.get();
	}

	public static void clear() {
		CONTEXT.remove();
	}

	public static String getUserId() {
		LoginUser user = get();
		return user != null ? user.getUserId() : "-";
	}
}
