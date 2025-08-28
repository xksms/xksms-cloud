package com.xksms.notify.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private String eventId; // 事件唯一ID
	private String recipientId; // 接收者ID (例如 userId)
	private String type; // 通知类型, e.g., "ORDER_SHIPPED"
	private String content; // 通知内容
	private LocalDateTime timestamp;
}