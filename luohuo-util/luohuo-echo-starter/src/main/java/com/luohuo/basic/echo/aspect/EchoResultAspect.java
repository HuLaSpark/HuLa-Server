package com.luohuo.basic.echo.aspect;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import com.luohuo.basic.annotation.echo.EchoResult;
import com.luohuo.basic.interfaces.echo.EchoService;

/**
 * InjectionResult 注解的 AOP 工具
 *
 * @author 乾乾
 * @date 2020年01月19日09:27:41
 */
@Aspect
@AllArgsConstructor
@Slf4j
public class EchoResultAspect {
    private final EchoService echoService;


    @Pointcut("@annotation(com.luohuo.basic.annotation.echo.EchoResult)")
    public void methodPointcut() {
    }


    @Around("methodPointcut()&&@annotation(ir)")
    public Object interceptor(ProceedingJoinPoint pjp, EchoResult ir) throws Throwable {
        Object proceed = pjp.proceed();
        echoService.action(proceed, ir.ignoreFields());
        return proceed;
    }
}
