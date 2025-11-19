package com.luohuo.basic.cloud.feign;

import com.alibaba.cloud.sentinel.feign.SentinelContractHolder;
import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.EntryType;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.context.ContextUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import feign.Feign;
import feign.FeignException;
import feign.InvocationHandlerFactory;
import feign.MethodMetadata;
import feign.Target;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import com.luohuo.basic.base.R;
import com.luohuo.basic.jackson.JsonUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;

import static feign.Util.checkNotNull;

/**
 * 自动降级, 代替原3.3.0之前版本的 GlobalFallbackFactoryProxy
 *
 * @author 乾乾
 * @see com.alibaba.cloud.sentinel.feign.SentinelInvocationHandler
 */
@Slf4j
public class LuohuoSentinelInvocationHandler implements InvocationHandler {

    public static final String EQUALS = "equals";

    public static final String HASH_CODE = "hashCode";

    public static final String TO_STRING = "toString";

    private final Target<?> target;

    private final Map<Method, InvocationHandlerFactory.MethodHandler> dispatch;

    private FallbackFactory<?> fallbackFactory;

    private Map<Method, Method> fallbackMethodMap;

    LuohuoSentinelInvocationHandler(Target<?> target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch,
									FallbackFactory<?> fallbackFactory) {
        this.target = checkNotNull(target, "target");
        this.dispatch = checkNotNull(dispatch, "dispatch");
        this.fallbackFactory = fallbackFactory;
        this.fallbackMethodMap = toFallbackMethod(dispatch);
    }

    LuohuoSentinelInvocationHandler(Target<?> target, Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
        this.target = checkNotNull(target, "target");
        this.dispatch = checkNotNull(dispatch, "dispatch");
    }

    static Map<Method, Method> toFallbackMethod(Map<Method, InvocationHandlerFactory.MethodHandler> dispatch) {
        Map<Method, Method> result = new LinkedHashMap<>();
        for (Method method : dispatch.keySet()) {
            method.setAccessible(true);
            result.put(method, method);
        }
        return result;
    }

    @Override
    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        if (EQUALS.equals(method.getName())) {
            try {
                Object otherHandler = args.length > 0 && args[0] != null
                        ? Proxy.getInvocationHandler(args[0]) : null;
                return equals(otherHandler);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else if (HASH_CODE.equals(method.getName())) {
            return hashCode();

        } else if (TO_STRING.equals(method.getName())) {
            return toString();
        } else {
            Object result;
            InvocationHandlerFactory.MethodHandler methodHandler = this.dispatch.get(method);
            // only handle by HardCodedTarget
            if (target instanceof Target.HardCodedTarget<?> hardCodedTarget) {
                MethodMetadata methodMetadata = SentinelContractHolder.METADATA_MAP
                        .get(hardCodedTarget.type().getName() + Feign.configKey(hardCodedTarget.type(), method));
                // resource default is HttpMethod:protocol://url
                if (methodMetadata == null) {
                    result = methodHandler.invoke(args);
                } else {
                    String resourceName = methodMetadata.template().method().toUpperCase() + ":" + hardCodedTarget.url()
                            + methodMetadata.template().path();
                    Entry entry = null;
                    try {
                        ContextUtil.enter(resourceName);
                        entry = SphU.entry(resourceName, EntryType.OUT, 1, args);
                        result = methodHandler.invoke(args);
                    } catch (Throwable ex) {
                        // fallback handle
                        if (!BlockException.isBlockException(ex)) {
                            Tracer.trace(ex);
                        }
                        if (fallbackFactory != null) {
                            try {
                                return fallbackMethodMap.get(method).invoke(fallbackFactory.create(ex), args);
                            } catch (IllegalAccessException e) {
                                // shouldn't happen as method is public due to being an
                                // interface
                                throw new AssertionError(e);
                            } catch (InvocationTargetException e) {
                                throw new AssertionError(e.getCause());
                            }
                        } else {
                            // 主要变化 by zuihou
                            if (R.class == method.getReturnType()) {
                                log.error("feign 内部服务调用异常", ex);
                                if (ex instanceof FeignException fex) {
                                    String responseBody = fex.contentUTF8();
                                    return JsonUtil.parse(responseBody, R.class);
                                }
                                return R.fail(ex.getLocalizedMessage());
                            } else {
                                throw ex;
                            }
                        }
                    } finally {
                        if (entry != null) {
                            entry.exit(1, args);
                        }
                        ContextUtil.exit();
                    }
                }
            } else {
                // other target type using default strategy
                result = methodHandler.invoke(args);
            }

            return result;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LuohuoSentinelInvocationHandler other) {
            return target.equals(other.target);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return target.hashCode();
    }

    @Override
    public String toString() {
        return target.toString();
    }
}
