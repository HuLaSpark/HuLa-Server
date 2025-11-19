package com.luohuo.basic.log.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import com.luohuo.basic.annotation.log.WebLog;
import com.luohuo.basic.utils.StrPool;

import java.lang.reflect.Method;

/**
 * 日志工具类
 *
 * @author 乾乾
 * @date 2019-04-28 11:30
 */
@Slf4j
public final class LogUtil {
    private LogUtil() {
    }

    /***
     * 获取操作信息
     */
    public static String getDescribe(JoinPoint point) {
        WebLog annotation = getTargetAnnotation(point);
        if (annotation == null) {
            return StrPool.EMPTY;
        }
        return annotation.value();
    }

    public static String getDescribe(WebLog annotation) {
        if (annotation == null) {
            return StrPool.EMPTY;
        }
        return annotation.value();
    }

    /**
     * 优先从子类获取 @WebLog：
     * 1，若子类重写了该方法，有标记就记录日志，没标记就忽略日志
     * 2，若子类没有重写该方法，就从父类获取，父类有标记就记录日志，没标记就忽略日志
     */
    public static WebLog getTargetAnnotation(JoinPoint point) {
        try {
            WebLog annotation = null;
            if (point.getSignature() instanceof MethodSignature ms) {
                Method method = ms.getMethod();
                if (method != null) {
                    annotation = method.getAnnotation(WebLog.class);
                }
            }
            return annotation;
        } catch (Exception e) {
            log.warn("获取 {}.{} 的 @WebLog 注解失败", point.getSignature().getDeclaringTypeName(), point.getSignature().getName(), e);
            return null;
        }
    }

}
