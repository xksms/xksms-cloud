package com.xksms.web.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class WebLogAspect {

	private final ObjectMapper objectMapper = new ObjectMapper();

	// 定义我们要在 MDC 中使用的 KEY
	private static final String TRACE_ID_KEY = "tid";
	private static final String IP_KEY = "ip";

	@Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
	public void webLog() {
	}

	@Around("webLog()")
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();

		// 1. 【核心改造】主动建立日志上下文
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

			// 主动从 SkyWalking API 获取 traceId，此时它应该已经可用了
			// 如果 Agent 还未创建，它会返回 "N/A"，我们依然记录
			String traceId = TraceContext.traceId();
			String clientIp = request.getRemoteAddr();

			// 手动将关键信息放入 MDC
			MDC.put(TRACE_ID_KEY, traceId);
			MDC.put(IP_KEY, clientIp);
		}

		try {
			// 2. 打印请求日志（此时 MDC 中已有 tid 和 ip）
			if (requestAttributes != null) {
				HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
				log.info("REQUEST  : {} {}", request.getMethod(), request.getRequestURL().toString());
				log.info("HANDLER  : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
				log.info("ARGS     : {}", objectMapper.writeValueAsString(joinPoint.getArgs()));
			}

			// 3. 执行目标方法
			Object result = joinPoint.proceed();

			// 4. 打印响应日志（此时 MDC 中同样有 tid 和 ip）
			if (requestAttributes != null) {
				long timeCost = System.currentTimeMillis() - startTime;
				log.info("RESPONSE : {}", objectMapper.writeValueAsString(result));
				log.info("TIME-COST: {} ms", timeCost);
			}
			return result;

		} finally {
			// 5. 【至关重要】在请求结束时，清理 MDC，防止内存泄漏
			MDC.clear();
		}
	}
}