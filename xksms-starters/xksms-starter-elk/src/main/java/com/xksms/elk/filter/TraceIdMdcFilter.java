package com.xksms.elk.filter;


import com.xksms.log.constants.LogConstant;
import com.xksms.log.utils.TraceIdGenerator;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.slf4j.MDC;

import java.io.IOException;

public class TraceIdMdcFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		try {
			String traceId = TraceIdGenerator.generate(); // 使用统一的 TraceIdGenerator
			MDC.put(LogConstant.TRACE_ID, traceId);
			chain.doFilter(request, response);
		} finally {
			MDC.clear();
		}
	}

	@Override
	public void destroy() {
	}
}
