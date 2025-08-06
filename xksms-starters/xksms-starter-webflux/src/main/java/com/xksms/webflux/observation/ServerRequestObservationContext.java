package com.xksms.webflux.observation;

import io.micrometer.observation.Observation;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * 通用的、用于服务器端HTTP请求的观测上下文。
 * 它携带了在观测过程中需要用到的核心对象。
 */
@Getter
@Setter
public class ServerRequestObservationContext extends Observation.Context {
	private final ServerHttpRequest request;
	private int statusCode;

	//用于存放观测开始时的纳秒级时间戳
	private long startTimeNanos;

	public ServerRequestObservationContext(ServerHttpRequest request) {
		this.request = request;
	}
}