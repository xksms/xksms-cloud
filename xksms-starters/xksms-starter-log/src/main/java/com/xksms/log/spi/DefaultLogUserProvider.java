package com.xksms.log.spi;

public class DefaultLogUserProvider implements LogUserProvider {
	@Override
	public String getCurrentUserId() {
		return "-";
	}
}
