package com.xksms.trace.reactor;


import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import reactor.core.CoreSubscriber;
import reactor.util.context.Context;
import reactor.util.context.ContextView;


public class MdcTraceSubscriber implements CoreSubscriber<Object> {
	// 与生产者约定好的 KEY
	private static final String TRACE_ID_KEY = "tid";
	private static final String IP_KEY = "ip";

	private final CoreSubscriber<Object> actual;

	public MdcTraceSubscriber(CoreSubscriber<Object> actual) {
		this.actual = actual;
	}

	// 【核心改造】 onNext 和 onError 都调用同一个桥接方法
	@Override
	public void onNext(Object o) {
		this.bridgeContextToMdcAndRun(() -> actual.onNext(o));
	}

	@Override
	public void onError(Throwable t) {
		this.bridgeContextToMdcAndRun(() -> actual.onError(t));
	}

	/**
	 * 将 Reactor Context 的值桥接到 MDC，并执行核心逻辑，然后清理 MDC。
	 * @param runnable 核心逻辑 (onNext / onError)
	 */
	private void bridgeContextToMdcAndRun(Runnable runnable) {
		// 从下游订阅者获取当前的 Reactor Context
		ContextView context = actual.currentContext();
		String traceId = context.getOrDefault(TRACE_ID_KEY, "N/A");
		String ip = context.getOrDefault(IP_KEY, "N/A");

		// 使用 try-with-resources 和 MDC.putCloseable 确保 MDC 被自动清理
		try (MDC.MDCCloseable tidMdc = MDC.putCloseable(TRACE_ID_KEY, traceId);
			 MDC.MDCCloseable ipMdc = MDC.putCloseable(IP_KEY, ip)) {
			runnable.run();
		}
	}

	// onSubscribe, onComplete, currentContext 方法保持不变
	@Override
	public void onSubscribe(Subscription s) {
		actual.onSubscribe(s);
	}

	@Override
	public void onComplete() {
		actual.onComplete();
	}

	@Override
	public Context currentContext() {
		return actual.currentContext();
	}
}
