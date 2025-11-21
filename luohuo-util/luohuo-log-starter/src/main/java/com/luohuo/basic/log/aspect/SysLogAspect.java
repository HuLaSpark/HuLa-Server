package com.luohuo.basic.log.aspect;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.luohuo.basic.annotation.log.WebLog;
import com.luohuo.basic.base.R;
import com.luohuo.basic.context.ContextConstants;
import com.luohuo.basic.context.ContextUtil;
import com.luohuo.basic.jackson.JsonUtil;
import com.luohuo.basic.log.event.SysLogEvent;
import com.luohuo.basic.log.util.LogUtil;
import com.luohuo.basic.log.util.ThreadLocalParam;
import com.luohuo.basic.model.log.OptLogDTO;
import com.luohuo.basic.utils.SpringUtils;
import com.luohuo.basic.utils.StrPool;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 操作日志使用spring event异步入库
 *
 * @author 乾乾
 * @date 2019-07-01 15:15
 */
@Slf4j
@Aspect
public class SysLogAspect {

    public static final int MAX_LENGTH = 65535;
    private static final ThreadLocal<OptLogDTO> THREAD_LOCAL = new ThreadLocal<>();
    private static final String FORM_DATA_CONTENT_TYPE = "multipart/form-data";
    /**
     * 用于SpEL表达式解析.
     */
    private final SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private final DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    /***
     * 定义controller切入点拦截规则：拦截标记WebLog注解和指定包下的方法
     * 2个表达式加起来才能拦截所有Controller 或者继承了BaseController的方法
     *
     * execution(public * com.luohuo.basic.base.controller.*.*(..)) 解释：
     * 第一个* 任意返回类型
     * 第二个* com.luohuo.basic.base.controller包下的所有类
     * 第三个* 类下的所有方法
     * ()中间的.. 任意参数
     *
     * \@annotation(com.luohuo.basic.annotation.log.WebLog) 解释：
     * 标记了@WebLog 注解的方法
     */
    @Pointcut("execution(public * com.luohuo.basic.base.controller.*.*(..)) || @annotation(com.luohuo.basic.annotation.log.WebLog)")
    public void sysLogAspect() {

    }

    /**
     * 返回通知
     *
     * @param ret       返回值
     * @param joinPoint 端点
     */
    @AfterReturning(returning = "ret", pointcut = "sysLogAspect()")
    public void doAfterReturning(JoinPoint joinPoint, Object ret) {
        tryCatch(p -> {
            WebLog sysLog = LogUtil.getTargetAnnotation(joinPoint);
            if (check(joinPoint, sysLog)) {
                return;
            }

            R r = Convert.convert(R.class, ret);
            OptLogDTO sysLogDTO = get();
            if (r == null) {
                sysLogDTO.setType("OPT");
                if (sysLog.response()) {
                    sysLogDTO.setResult(getText(String.valueOf(ret == null ? StrPool.EMPTY : ret)));
                }
            } else {
                if (r.getsuccess()) {
                    sysLogDTO.setType("OPT");
                } else {
                    sysLogDTO.setType("EX");
                    sysLogDTO.setExDetail(r.getMsg());
                }
                if (sysLog.response()) {
                    sysLogDTO.setResult(getText(r.toString()));
                }
            }

            publishEvent(sysLogDTO);
        });

    }

    /**
     * 异常通知
     *
     * @param joinPoint 端点
     * @param e         异常
     */
    @AfterThrowing(pointcut = "sysLogAspect()", throwing = "e")
    public void doAfterThrowable(JoinPoint joinPoint, Throwable e) {
        tryCatch((aaa) -> {
            WebLog sysLog = LogUtil.getTargetAnnotation(joinPoint);
            if (check(joinPoint, sysLog)) {
                return;
            }

            OptLogDTO optLogDTO = get();
            optLogDTO.setType("EX");

            // 遇到错误时，请求参数若为空，则记录
            if (!sysLog.request() && sysLog.requestByError() && StrUtil.isEmpty(optLogDTO.getParams())) {
                Object[] args = joinPoint.getArgs();
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                String strArgs = getArgs(args, request);
                optLogDTO.setParams(getText(strArgs));
            }

            // 异常对象
            optLogDTO.setExDetail(ExceptionUtil.stacktraceToString(e, MAX_LENGTH));

            publishEvent(optLogDTO);
        });
    }

    /**
     * 执行方法之前
     *
     * @param joinPoint 端点
     */
    @Before(value = "sysLogAspect()")
    public void doBefore(JoinPoint joinPoint) {
        tryCatch(val -> {
            WebLog sysLog = LogUtil.getTargetAnnotation(joinPoint);
            if (check(joinPoint, sysLog)) {
                return;
            }
            OptLogDTO optLogDTO = buildOptLogDTO(joinPoint, sysLog);
            THREAD_LOCAL.set(optLogDTO);
        });
    }

    @NonNull
    private OptLogDTO buildOptLogDTO(JoinPoint joinPoint, WebLog sysLog) {
        // 开始时间
        OptLogDTO optLogDTO = get();
        optLogDTO.setCreateBy(ContextUtil.getUserId());
        setDescription(joinPoint, sysLog, optLogDTO);
        // 类名
        optLogDTO.setClassPath(joinPoint.getTarget().getClass().getName());
        //获取执行的方法名
        optLogDTO.setActionMethod(joinPoint.getSignature().getName());

        HttpServletRequest request = setParams(joinPoint, sysLog, optLogDTO);
        optLogDTO.setRequestIp(JakartaServletUtil.getClientIP(request));
        optLogDTO.setRequestUri(URLUtil.getPath(request.getRequestURI()));
        optLogDTO.setHttpMethod(request.getMethod());
        optLogDTO.setUa(StrUtil.sub(request.getHeader("user-agent"), 0, 500));
        if (ContextUtil.getBoot()) {
            optLogDTO.setCreatedOrgId(ContextUtil.getCurrentCompanyId());
            optLogDTO.setToken(ContextUtil.getToken());
        } else {
            optLogDTO.setToken(Convert.toStr(request.getHeader(ContextConstants.TOKEN_HEADER)));
            optLogDTO.setCreatedOrgId(Convert.toLong(request.getHeader(ContextConstants.CURRENT_COMPANY_ID_HEADER)));
        }
        optLogDTO.setTrace(MDC.get(ContextConstants.TRACE_ID_HEADER));
        if (StrUtil.isEmpty(optLogDTO.getTrace())) {
            optLogDTO.setTrace(request.getHeader(ContextConstants.TRACE_ID_HEADER));
        }
        optLogDTO.setStartTime(LocalDateTime.now());
        return optLogDTO;
    }

    @NonNull
    private HttpServletRequest setParams(JoinPoint joinPoint, WebLog sysLog, OptLogDTO optLogDTO) {
        // 参数
        Object[] args = joinPoint.getArgs();

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes(), "只能在Spring Web环境使用@WebLog记录日志")).getRequest();
        if (sysLog.request()) {
            String strArgs = getArgs(args, request);
            optLogDTO.setParams(getText(strArgs));
        }
        return request;
    }

    private void setDescription(JoinPoint joinPoint, WebLog sysLog, OptLogDTO optLogDTO) {
        String controllerDescription = "";
        Tag api = joinPoint.getTarget().getClass().getAnnotation(Tag.class);
        if (api != null) {
            controllerDescription = api.name();
        }

        String controllerMethodDescription = LogUtil.getDescribe(sysLog);

        if (StrUtil.isNotEmpty(controllerMethodDescription) && StrUtil.contains(controllerMethodDescription, StrPool.HASH)) {
            //获取方法参数值
            Object[] args = joinPoint.getArgs();

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            controllerMethodDescription = getValBySpEl(controllerMethodDescription, methodSignature, args);
        }

        if (StrUtil.isEmpty(controllerDescription)) {
            optLogDTO.setDescription(controllerMethodDescription);
        } else {
            if (sysLog.controllerApiValue()) {
                optLogDTO.setDescription(controllerDescription + "-" + controllerMethodDescription);
            } else {
                optLogDTO.setDescription(controllerMethodDescription);
            }
        }
    }


    private OptLogDTO get() {
        OptLogDTO sysLog = THREAD_LOCAL.get();
        if (sysLog == null) {
            return new OptLogDTO();
        }
        return sysLog;
    }

    private void tryCatch(Consumer<String> consumer) {
        try {
            consumer.accept("");
        } catch (Exception e) {
            log.warn("记录操作日志异常", e);
            THREAD_LOCAL.remove();
        }
    }

    private void publishEvent(OptLogDTO sysLog) {
        sysLog.setFinishTime(LocalDateTime.now());
        sysLog.setConsumingTime(sysLog.getStartTime().until(sysLog.getFinishTime(), ChronoUnit.MILLIS));
        SpringUtils.publishEvent(new SysLogEvent(sysLog));
        THREAD_LOCAL.remove();
    }

    /**
     * 监测是否需要记录日志
     *
     * @param joinPoint 端点
     * @param sysLog    操作日志
     * @return true 表示不需要记录日志
     */
    private boolean check(JoinPoint joinPoint, WebLog sysLog) {
        if (sysLog == null || !sysLog.enabled()) {
            return true;
        }
        // 读取目标类上的注解
        WebLog targetClass = joinPoint.getTarget().getClass().getAnnotation(WebLog.class);
        // 加上 sysLog == null 会导致父类上的方法永远需要记录日志
        return targetClass != null && !targetClass.enabled();
    }

    /**
     * 截取指定长度的字符串
     *
     * @param val 参数
     * @return 截取文本
     */
    private String getText(String val) {
        return StrUtil.sub(val, 0, 65535);
    }

    private String getArgs(Object[] args, HttpServletRequest request) {
        String strArgs = StrPool.EMPTY;
        Object[] params = Arrays.stream(args).filter(item -> !(item instanceof ServletRequest || item instanceof ServletResponse)).toArray();

        try {
            if (!request.getContentType().contains(FORM_DATA_CONTENT_TYPE)) {
                strArgs = JsonUtil.toJson(params);
            }
        } catch (Exception e) {
            try {
                strArgs = Arrays.toString(params);
            } catch (Exception ex) {
                log.warn("解析参数异常", ex);
            }
        }
        return strArgs;
    }

    /**
     * 解析spEL表达式
     */
    private String getValBySpEl(String spEl, MethodSignature methodSignature, Object[] args) {
        try {
            //获取方法形参名数组
            String[] paramNames = nameDiscoverer.getParameterNames(methodSignature.getMethod());
            if (paramNames != null && paramNames.length > 0) {
                Expression expression = spelExpressionParser.parseExpression(spEl);
                // spring的表达式上下文对象
                EvaluationContext context = new StandardEvaluationContext();
                // 给上下文赋值
                for (int i = 0; i < args.length; i++) {
                    context.setVariable(paramNames[i], args[i]);
                    context.setVariable("p" + i, args[i]);
                }
                ThreadLocalParam tlp = new ThreadLocalParam();
                BeanUtil.fillBeanWithMap(ContextUtil.getLocalMap(), tlp, true);
                context.setVariable("threadLocal", tlp);
                Object value = expression.getValue(context);
                return value == null ? spEl : value.toString();
            }
        } catch (Exception e) {
            log.warn("解析操作日志的el表达式出错", e);
        }
        return spEl;
    }


}
