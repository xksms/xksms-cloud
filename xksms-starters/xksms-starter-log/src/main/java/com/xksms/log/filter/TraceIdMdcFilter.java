package com.xksms.log.filter;

import com.xksms.log.constants.LogConstant;
import com.xksms.log.spi.LogUserProvider;
import com.xksms.log.utils.TraceIdGenerator;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.slf4j.MDC;

import java.io.IOException;

public class TraceIdMdcFilter implements Filter {

	private final LogUserProvider logUserProvider;

	public TraceIdMdcFilter(LogUserProvider logUserProvider) {
		this.logUserProvider = logUserProvider;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {
			MDC.put(LogConstant.TRACE_ID, TraceIdGenerator.generate());
			MDC.put(LogConstant.USER_ID, logUserProvider.getCurrentUserId());
			chain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}
}

