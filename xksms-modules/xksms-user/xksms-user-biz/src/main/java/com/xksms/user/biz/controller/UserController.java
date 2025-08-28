package com.xksms.user.biz.controller;

import com.xksms.common.core.BaseException;
import com.xksms.common.enums.GlobalErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

	/**
	 * 模拟成功获取用户信息的场景
	 */
	@GetMapping("/{id}")
	public UserDTO getUserById(@PathVariable Long id) {
		// [关键] 注意，我们这里直接返回业务对象 UserDTO，
		// 而不是 Result<UserDTO>。封装将由 GlobalResponseAdvice 自动完成。
		return new UserDTO(id, "MockUser-" + id);
	}

	/**
	 * 模拟用户不存在的业务异常场景
	 */
	@GetMapping("/find-non-existent")
	public void findNonExistentUser() {
		// [关键] 我们直接抛出自定义的 BaseException，
		// 并传入一个定义好的错误码。捕获和封装将由 GlobalExceptionHandler 自动完成。
		throw new BaseException(GlobalErrorCodeEnum.RESOURCE_NOT_FOUND);
	}

	// --- 临时的 DTO 对象 ---
	@Data
	@AllArgsConstructor
	static class UserDTO {
		private Long id;
		private String username;
	}
}