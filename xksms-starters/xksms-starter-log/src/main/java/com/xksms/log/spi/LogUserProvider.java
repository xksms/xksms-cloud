package com.xksms.log.spi;

@FunctionalInterface
public interface LogUserProvider {
	/**
	 * 提供当前用户ID给日志系统使用，如账号ID、用户名
	 */
	String getCurrentUserId();
}
