package com.xksms.user.interfaces.rest.internal;

import com.xksms.common.core.Result;
import com.xksms.common.enums.GlobalErrorCodeEnum;
import com.xksms.user.api.dto.UserAuthDTO;
import com.xksms.user.application.service.UserAuthApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * 内部提供给认证中心的租户账户查询接口。
 */
@RestController
@RequestMapping("/internal/tenants")
@Validated
public class UserAuthController {

    private static final Logger log = LoggerFactory.getLogger(UserAuthController.class);

    private final UserAuthApplicationService userAuthApplicationService;

    public UserAuthController(UserAuthApplicationService userAuthApplicationService) {
        this.userAuthApplicationService = userAuthApplicationService;
    }

    @GetMapping("/{tenantId}/users/{username}")
    public Result<UserAuthDTO> getUserAuthDetails(@PathVariable("tenantId") String tenantId,
            @PathVariable("username") String username) {
        Optional<UserAuthDTO> dtoOptional = userAuthApplicationService.loadAuthDetails(tenantId, username);
        if (dtoOptional.isEmpty()) {
            log.warn("User [{}] not found or disabled under tenant [{}]", username, tenantId);
            return Result.failure(GlobalErrorCodeEnum.RESOURCE_NOT_FOUND, "用户不存在或已禁用");
        }
        return Result.success(dtoOptional.get());
    }
}
