package com.luohuo.basic.tenant.core.aop;

import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.utils.spring.SpringExpressionUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import com.luohuo.basic.tenant.core.util.TenantUtils;

/**
 * 忽略多租户的 Aspect，基于 {@link TenantIgnore} 注解实现，用于一些全局的逻辑。
 * 例如说，一个定时任务，读取所有数据，进行处理。
 * 又例如说，读取所有数据，进行缓存。
 *
 * 整体逻辑的实现，和 {@link TenantUtils#executeIgnore(Runnable)} 需要保持一致
 */
@Aspect
@Slf4j
public class TenantIgnoreAspect {

    @Around("@annotation(tenantIgnore)")
    public Object around(ProceedingJoinPoint joinPoint, TenantIgnore tenantIgnore) throws Throwable {
		log.debug("当前上下文: {}", ContextUtil.getLocalMap());
        Boolean oldIgnore = ContextUtil.isIgnore();
        try {
            // 计算条件，满足的情况下，才进行忽略
            Object enable = SpringExpressionUtils.parseExpression(tenantIgnore.enable());
            if (Boolean.TRUE.equals(enable)) {
                ContextUtil.setIgnore(true);
            }

            // 执行逻辑
            return joinPoint.proceed();
        } finally {
            ContextUtil.setIgnore(oldIgnore);
        }
    }

}
