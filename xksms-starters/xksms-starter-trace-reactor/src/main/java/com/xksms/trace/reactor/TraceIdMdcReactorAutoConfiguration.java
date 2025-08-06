package com.xksms.trace.reactor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Hooks;

@Configuration
@ConditionalOnClass(Hooks.class)
@Import(TraceIdMdcHookRegistrar.class)
public class TraceIdMdcReactorAutoConfiguration {
}
