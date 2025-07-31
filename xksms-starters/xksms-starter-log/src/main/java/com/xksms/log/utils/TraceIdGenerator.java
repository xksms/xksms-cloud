package com.xksms.log.utils;

import java.util.UUID;

public class TraceIdGenerator {
	public static String generate() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
