package com.luohuo.basic.tenant.core.web;

import cn.hutool.core.util.StrUtil;
import com.luohuo.basic.boot.utils.WebUtils;
import com.luohuo.basic.context.ContextConstants;
import com.luohuo.basic.context.ContextUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 多租户 Context Web 过滤器
 * 将请求 Header 中的 tenant-id 解析出来，添加到 {@link ContextUtil} 中，这样后续的 DB 等操作，可以获得到租户编号。
 */
public class TenantContextWebFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
			String tenantId = WebUtils.getHeader(request, ContextConstants.HEADER_TENANT_ID);
			if (StrUtil.isNotEmpty(tenantId)){
				ContextUtil.setTenantId(Long.parseLong(tenantId));
			}
            chain.doFilter(request, response);
        } finally {
            // 清理
            ContextUtil.clearTenantContext();
        }
    }

}
