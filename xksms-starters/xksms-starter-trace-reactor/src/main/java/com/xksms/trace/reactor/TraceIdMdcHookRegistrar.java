package com.xksms.trace.reactor;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Operators;

@Slf4j
@Component
public class TraceIdMdcHookRegistrar implements ApplicationListener<ApplicationReadyEvent> {

	private static final String HOOK_KEY = "xksms-mdc-trace-hook";

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		Hooks.onEachOperator(HOOK_KEY, Operators.lift((scannable, subscriber) ->
				new MdcTraceSubscriber(subscriber)));
		log.info("注册 Reactor traceId → MDC Hook 完成");
	}

	@PreDestroy
	public void cleanupHook() {
		Hooks.resetOnEachOperator(HOOK_KEY);
		log.info("[XKSMS] Reactor MDC traceId hook removed.");
	}


}
