package com.xksms.auth.service;

//import com.xksms.common.core.Result;
//import com.xksms.user.api.dto.UserAuthDTO;
//import com.xksms.user.api.feign.UserFeignClient;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RemoteUserDetailsService implements UserDetailsService {

//    private final UserFeignClient userFeignClient;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		Result<UserAuthDTO> result = userFeignClient.getUserDetailsByUsername(username);
//
//		if (result == null || result.code() != 200 || result.data() == null) {
//			throw new UsernameNotFoundException("用户不存在或用户服务异常");
//		}
//
//		UserAuthDTO userAuth = result.data();
//
		return User.builder()
//				.username(userAuth.getUsername())
//				.password(userAuth.getPassword())
//				// [核心修正] 将 Set<String> 转换为 String[] 数组
//				.authorities(userAuth.getAuthorities().toArray(new String[0]))
//				.disabled(!userAuth.isEnabled())
				.accountExpired(false)
				.credentialsExpired(false)
				.accountLocked(false)
				.build();
	}
}