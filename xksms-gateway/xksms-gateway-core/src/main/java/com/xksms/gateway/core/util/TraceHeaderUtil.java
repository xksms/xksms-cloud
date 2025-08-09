// 位置: xksms-gateway/src/main/java/com/xksms/gateway/util/TraceHeaderUtil.java
package com.xksms.gateway.core.util;


import com.xksms.common.constant.LogConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 链路追踪相关 Header 的解析与生成工具类。
 * <p>
 * 这个工具类的核心职责是作为网关的“护照解析官”，
 * 负责在系统边界识别、验证、生成符合规范的链路追踪标识。
 * 它遵循“标准优先、兼容并包”的原则。
 */
@Slf4j
public final class TraceHeaderUtil {

	private TraceHeaderUtil() {
	}

	/**
	 * 从请求头中提取一个唯一的 TraceId。
	 * 这是本工具类的主要入口方法。
	 * <p>
	 * 提取顺序:
	 * 1. 尝试从 W3C 标准的 {@link LogConstant#TRACE_PARENT_HEADER} 中解析。
	 * 2. 如果失败，尝试从 SkyWalking 的 {@link LogConstant#SW8_HEADER} 中解析。
	 * 3. 如果都失败，则生成一个全新的、符合 W3C 规范的 TraceId。
	 *
	 * @param headers 请求头
	 * @return 规范化的 TraceId (32位小写16进制字符串)
	 */
	public static String extractTraceId(HttpHeaders headers) {
		return getFromTraceParent(headers)
				.or(() -> getFromSw8(headers))
				.orElseGet(TraceHeaderUtil::generateTraceId);
	}

	/**
	 * 构建一个符合 W3C 规范的 traceparent header 值。
	 *
	 * @param traceId 32位 traceId
	 * @param spanId 16位 spanId
	 * @return 符合 W3C 格式的 traceparent 字符串 (例如: 00-4bf92f3577b34da6a3ce929d0e0e4736-00f067aa0ba902b7-01)
	 */
	public static String buildTraceParentHeader(String traceId, String spanId) {
		// 格式: version-traceid-spanid-flags
		// 00 - 版本号，目前固定为 00
		// 01 - 采样标志 (01 表示已采样, 00 表示未采样)
		return "00-" + traceId + "-" + spanId + "-01";
	}

	/**
	 * 生成一个符合 W3C 规范的随机 TraceId (16字节，32位小写16进制字符串)。
	 *
	 * @return 32位小写16进制字符串
	 */
	public static String generateTraceId() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 生成一个符合 W3C 规范的随机 SpanId (8字节，16位小写16进制字符串)。
	 *
	 * @return 16位小写16进制字符串
	 */
	public static String generateSpanId() {
		byte[] bytes = new byte[8];
		ThreadLocalRandom.current().nextBytes(bytes);
		var sb = new StringBuilder(16);
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}

	/**
	 * 尝试从 W3C traceparent header 中解析 trace-id。
	 *
	 * @param headers 请求头
	 * @return 如果解析成功，返回包含 trace-id 的 Optional，否则返回空
	 */
	private static Optional<String> getFromTraceParent(HttpHeaders headers) {
		String traceParent = headers.getFirst(LogConstant.TRACE_PARENT_HEADER);
		if (StringUtils.hasText(traceParent)) {
			String[] parts = traceParent.split("-");
			// 合法的 traceparent 必须有4部分，且 trace-id 和 span-id 不能是全0
			if (parts.length == 4 && StringUtils.hasText(parts[1]) && !Objects.equals(parts[1], "00000000000000000000000000000000")) {
				return Optional.of(parts[1]);
			}
		}
		return Optional.empty();
	}

	/**
	 * 尝试从 SkyWalking sw8 header 中解析 trace-id。
	 *
	 * @param headers 请求头
	 * @return 如果解析成功，返回包含 trace-id 的 Optional，否则返回空
	 */
	private static Optional<String> getFromSw8(HttpHeaders headers) {
		String sw8 = headers.getFirst(LogConstant.SW8_HEADER);
		// sw8 格式: 1(sampled)-{traceId(base64)}-{segmentId(base64)}-{spanId}-{...}
		if (StringUtils.hasText(sw8)) {
			try {
				String[] parts = sw8.split("-");
				if (parts.length > 1) {
					// traceId 是第二部分，经过 Base64 编码
					String decodedTraceId = new String(Base64.getDecoder().decode(parts[1]));
					// SkyWalking 的 traceId 是一个包含三个'.'的全局唯一ID，我们需要将其转换为无符号的字符串
					return Optional.of(decodedTraceId.replace(".", ""));
				}
			} catch (Exception e) {
				log.warn("Failed to parse sw8 header [{}]. Error: {}", sw8, e.getMessage());
				return Optional.empty();
			}
		}
		return Optional.empty();
	}
}