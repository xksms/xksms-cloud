package com.xksms.observability.config;

import io.micrometer.observation.ObservationRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

/**
 * MDC 上下文自动配置类
 *
 * Spring Boot 3.2+ Micrometer Tracing 已经自动处理了：
 * 1. traceId 注入到 MDC (通过 SLF4J)
 * 2. Reactor Context 传播 (通过 hooks)
 * 3. 线程切换时的上下文保持
 *
 * 本配置类仅用于确保 ObservationRegistry 被正确初始化
 */
@AutoConfiguration
@ConditionalOnClass(ObservationRegistry.class)
public class MdcContextAutoConfiguration {
	// 无需额外配置，Spring Boot 自动处理
}
