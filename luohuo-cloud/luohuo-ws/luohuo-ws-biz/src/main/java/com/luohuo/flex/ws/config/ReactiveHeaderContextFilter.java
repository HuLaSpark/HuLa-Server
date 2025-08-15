package com.luohuo.flex.ws.config;

import com.luohuo.basic.context.ContextConstants;
import com.luohuo.flex.ws.ReactiveContextUtil;
import com.luohuo.flex.ws.rocketmq.TenantRocketMQInitializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFlux 响应式上下文过滤器、MQ租户拦截器配置
 * 将请求头中的数据封装到 ReactiveContextUtil
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 确保最高优先级
public class ReactiveHeaderContextFilter implements WebFilter {

	@Bean
	@ConditionalOnClass(name = "org.apache.rocketmq.spring.core.RocketMQTemplate")
	public TenantRocketMQInitializer tenantRocketMQInitializer() {
		return new TenantRocketMQInitializer();
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		log.info("ReactiveHeaderContextFilter url={}, method={}", request.getURI().getPath(), request.getMethod());

		String uid = request.getHeaders().getFirst(ContextConstants.JWT_KEY_U_ID);
		String tenantId = request.getHeaders().getFirst(ContextConstants.HEADER_TENANT_ID);
		if (StringUtils.isNotEmpty(uid)){
			ReactiveContextUtil.setUid(Long.parseLong(uid));
		}
		if (StringUtils.isNotEmpty(tenantId)){
			ReactiveContextUtil.setTenantId(Long.parseLong(tenantId));
		}
		return chain.filter(exchange).doFinally(signalType -> ReactiveContextUtil.remove());
	}


}