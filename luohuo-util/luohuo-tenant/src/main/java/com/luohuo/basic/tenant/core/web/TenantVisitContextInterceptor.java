package com.luohuo.basic.tenant.core.web;

import cn.hutool.core.util.ObjUtil;
import com.luohuo.basic.boot.utils.WebFrameworkUtils;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.tenant.config.TenantProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@RequiredArgsConstructor
@Slf4j
public class TenantVisitContextInterceptor implements HandlerInterceptor {

    private static final String PERMISSION = "system:tenant:visit";

    private final TenantProperties tenantProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果和当前租户编号一致，则直接跳过
        Long visitTenantId = WebFrameworkUtils.getVisitTenantId(request);
        if (visitTenantId == null) {
            return true;
        }
        if (ObjUtil.equal(visitTenantId, ContextUtil.getTenantId())) {
            return true;
        }
        // 必须是登录用户
        Long uid = ContextUtil.getUid();
        if (uid == null) {
            return true;
        }

        // 【重点】切换租户编号
        ContextUtil.setVisitTenantId(visitTenantId);
        ContextUtil.setTenantId(visitTenantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 【重点】清理切换，换回原租户编号
        if (ContextUtil.getUid() != null && ContextUtil.getTenantId() != null) {
            ContextUtil.setTenantId(ContextUtil.getTenantId());
        }
    }

}
