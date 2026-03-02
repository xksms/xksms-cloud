package com.xksms.observability.handler;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

/**
 * 链路追踪 MDC 注入处理器
 *
 * 工作原理：
 * 1. Spring Boot 3.2+ Micrometer Tracing 会自动将 traceId 注入到 Reactor Context
 * 2. 通过 logback 的 MDC 过滤器，自动从 Reactor Context 读取 traceId
 * 3. 无需手动处理，Spring 已经自动桥接
 *
 * 此 Handler 用于确保在 Observation 启动时正确设置 MDC
 */
@Component
@ConditionalOnClass({ObservationRegistry.class, Tracer.class})
public class MdcInjectingObservationHandler implements ObservationHandler<Observation.Context> {

    private static final String TRACE_ID_KEY = "traceId";
    private static final String SPAN_ID_KEY = "spanId";

    private final Tracer tracer;

    public MdcInjectingObservationHandler(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void onStart(Observation.Context context) {
        // 从当前 span 获取 traceId 和 spanId
        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            String traceId = currentSpan.context().traceId();
            String spanId = currentSpan.context().spanId();

            // 注入 MDC (对 MVC 有效)
            MDC.put(TRACE_ID_KEY, traceId);
            MDC.put(SPAN_ID_KEY, spanId);
        }
    }

    @Override
    public void onStop(Observation.Context context) {
        // 清理 MDC
        MDC.remove(TRACE_ID_KEY);
        MDC.remove(SPAN_ID_KEY);
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        // 仅处理有 Span 的场景
        return context.getContextualName() != null;
    }
}
