package com.luohuo.flex.ws;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.luohuo.basic.context.ContextConstants;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;
import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * 响应式编程上下文工具类 - 支持跨线程、跨响应式链路传递上下文
 */
public class ReactiveContextUtil {

	private static final String CONTEXT_KEY = "REACTIVE_CONTEXT";

	// ============== TTL 存储 ==============
	private static final TransmittableThreadLocal<Map<String, String>> TTL_CONTEXT =
			new TransmittableThreadLocal<>() {
				@Override
				protected Map<String, String> initialValue() {
					return new ConcurrentHashMap<>(10);
				}
			};

	public static Executor getContextAwareExecutor(Executor executor) {
		return TtlExecutors.getTtlExecutor(executor);
	}

	/**
	 * 写入响应式上下文
	 *
	 * @param key   上下文键
	 * @param value 上下文值
	 * @return 更新后的 Reactor Context
	 */
	public static Context writeToReactorContext(String key, Object value) {
		return Context.of(CONTEXT_KEY, new ConcurrentHashMap<String, String>() {{
			put(key, value.toString());
		}});
	}

	/**
	 * 写入完整上下文到 Reactor
	 */
	public static Context writeFullContext(Map<String, String> context) {
		return Context.of(CONTEXT_KEY, new ConcurrentHashMap<>(context));
	}

	/**
	 * 从 Reactor Context 注入到 TTL
	 */
	public static void injectContextFromReactor() {
		// 确保在响应式链的 Context 中有值
		Map<String, String> context = Mono.deferContextual(ctxView ->
				Mono.just(ctxView.<Map<String, String>>getOrDefault(CONTEXT_KEY, null))
		).block();

		if (context != null) {
			TTL_CONTEXT.set(new ConcurrentHashMap<>(context));
		}
	}

	/**
	 * 获取 Reactor Context 包装的 Mono
	 */
	public static Mono<Map<String, String>> getReactiveContext() {
		return Mono.deferContextual(ctxView ->
				Mono.just(ctxView.getOrDefault(CONTEXT_KEY, null))
		);
	}

	/**
	 * 获取当前 TTL 上下文
	 */
	public static Map<String, String> getTTLContext() {
		return TTL_CONTEXT.get();
	}

	/**
	 * 设置 TTL 上下文值
	 */
	public static void setTTLValue(String key, Object value) {
		if (value != null) {
			TTL_CONTEXT.get().put(key, value.toString());
		}
	}

	/**
	 * 获取 TTL 上下文值
	 */
	public static <T> T getTTLValue(String key, Class<T> type) {
		String value = TTL_CONTEXT.get().get(key);
		return convertValue(value, type);
	}

	/**
	 * 清除上下文
	 */
	public static void remove() {
		TTL_CONTEXT.remove();
	}

	private static <T> T convertValue(String value, Class<T> type) {
		if (type == String.class) {
			return type.cast(value != null ? value : "");
		} else if (type == Long.class) {
			return type.cast(value != null ? Long.parseLong(value) : null);
		}
		return null;
	}

	public static Long getTenantId() {
		return getTTLValue(ContextConstants.HEADER_TENANT_ID, Long.class);
	}

	public static void setTenantId(Long tenantId) {
		setTTLValue(ContextConstants.HEADER_TENANT_ID, tenantId);
	}

	public static void setUid(Long uid) {
		setTTLValue(ContextConstants.JWT_KEY_U_ID, uid);
	}

	public static Long getUid() {
		return getTTLValue(ContextConstants.U_ID_HEADER, Long.class);
	}
}