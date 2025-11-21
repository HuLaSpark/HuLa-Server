package com.luohuo.basic.tenant.core.rpc;

import com.luohuo.basic.context.ContextUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import static com.luohuo.basic.context.ContextConstants.HEADER_TENANT_ID;


/**
 * Tenant 的 RequestInterceptor 实现类：Feign 请求时，将 {@link ContextUtil} 设置到 header 中，继续透传给被调用的服务
 *
 * @author 乾乾
 */
public class TenantRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Long tenantId = ContextUtil.getTenantId();
        if (tenantId != null) {
            requestTemplate.header(HEADER_TENANT_ID, String.valueOf(tenantId));
        }
    }

}
