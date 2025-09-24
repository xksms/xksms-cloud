package com.xksms.auth.infrastructure.tenant.filter;

import com.xksms.auth.config.AuthServerProperties;
import com.xksms.auth.domain.tenant.Tenant;
import com.xksms.auth.domain.tenant.TenantId;
import com.xksms.auth.infrastructure.tenant.TenantContextHolder;
import com.xksms.auth.infrastructure.tenant.TenantRegistry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * 在请求入口解析租户标识并注入上下文。
 */
public class TenantContextFilter extends OncePerRequestFilter {

    public static final String TENANT_HEADER = "X-Tenant-Id";
    public static final String TENANT_REQUEST_ATTRIBUTE = TenantContextFilter.class.getName() + ".TENANT";

    private static final Logger log = LoggerFactory.getLogger(TenantContextFilter.class);

    private final TenantRegistry tenantRegistry;
    private final String defaultTenantId;

    public TenantContextFilter(TenantRegistry tenantRegistry, AuthServerProperties properties) {
        this.tenantRegistry = tenantRegistry;
        this.defaultTenantId = Optional.ofNullable(properties.getDefaultTenant()).filter(StringUtils::hasText)
                .orElse(TenantId.DEFAULT_VALUE);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String resolvedTenantId = resolveTenantId(request);
        Optional<Tenant> tenantOptional = tenantRegistry.findTenant(resolvedTenantId);

        if (tenantOptional.isEmpty() || !tenantOptional.get().isEnabled()) {
            log.warn("Rejecting request because tenant [{}] is not registered or disabled", resolvedTenantId);
            response.sendError(HttpStatus.BAD_REQUEST.value(), "Invalid tenant identifier");
            return;
        }

        Tenant tenant = tenantOptional.get();
        TenantContextHolder.setTenantId(tenant.getId());
        request.setAttribute(TENANT_REQUEST_ATTRIBUTE, tenant.getId().value());

        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContextHolder.clear();
        }
    }

    private String resolveTenantId(HttpServletRequest request) {
        String tenantFromHeader = request.getHeader(TENANT_HEADER);
        if (StringUtils.hasText(tenantFromHeader)) {
            return tenantFromHeader.trim();
        }

        String tenantFromParam = request.getParameter("tenantId");
        if (!StringUtils.hasText(tenantFromParam)) {
            tenantFromParam = request.getParameter("tenant");
        }
        if (StringUtils.hasText(tenantFromParam)) {
            return tenantFromParam.trim();
        }

        String host = request.getServerName();
        if (StringUtils.hasText(host) && host.contains(".")) {
            String candidate = host.substring(0, host.indexOf('.'));
            if (StringUtils.hasText(candidate) && !"www".equalsIgnoreCase(candidate)) {
                return candidate;
            }
        }

        return defaultTenantId;
    }
}
