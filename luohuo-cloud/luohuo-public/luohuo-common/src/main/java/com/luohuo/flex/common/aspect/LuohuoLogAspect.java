package com.luohuo.flex.common.aspect;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.utils.StrPool;
import com.luohuo.flex.common.properties.SystemProperties;

/**
 * 操作日志使用spring event异步入库
 *
 * @author 乾乾
 * @date 2019-07-01 15:15
 */
@Slf4j
@Aspect
@RequiredArgsConstructor
public class LuohuoLogAspect extends BaseLogAspect {
    private final SystemProperties systemProperties;

    /***
     * 定义controller切入点拦截规则：拦截标记SysLog注解和指定包下的方法
     * 2个表达式加起来才能拦截所有Controller 或者继承了BaseController的方法
     *
     * execution(public * com.luohuo.flex.*.*(..)) 解释：
     * 第一个* 任意返回类型
     * 第二个* com.luohuo.flex 包下的所有类
     * 第三个* 类下的所有方法
     * ()中间的.. 任意参数
     *
     */
    @Pointcut("execution(* com.luohuo.flex.controller..*.*(..))) || execution(* com.luohuo.flex.service..*.*(..))) " +
            "|| execution(* com.luohuo.flex.biz..*.*(..)))")
    public void luohuoLogAspect() {

    }

    @Around("luohuoLogAspect()")
    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        String logTraceId = ContextUtil.getLogTraceId() == null ? StrPool.EMPTY : ContextUtil.getLogTraceId();
        String types = getParamTypes(joinPoint);

        outArgsLog(joinPoint, logTraceId, types, joinPoint.getArgs(), systemProperties.getRecordLuohuoArgs());
        long start = System.currentTimeMillis();
        try {
            Object retVal = joinPoint.proceed();
            outResultLog(joinPoint, logTraceId, types, start, retVal, systemProperties.getRecordLuohuoResult());
            return retVal;
        } catch (Exception e) {
            log.error("<<<< [traceId:{}] {}.{}({}) end... {} ms", logTraceId, joinPoint.getSignature().getDeclaringType(),
                    joinPoint.getSignature().getName(), types, System.currentTimeMillis() - start, e);
            throw e;
        }
    }

}
