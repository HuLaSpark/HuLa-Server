package com.luohuo.flex.gateway.filter;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import com.luohuo.basic.context.ContextConstants;

/**
 * 生成日志链路追踪id，并传入header中
 *
 * @author 乾乾
 * @date 2020年03月09日18:02:47
 */
@Component
public class TraceFilter implements WebFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        //链路追踪id
        String traceId = IdUtil.fastSimpleUUID();
        MDC.put(ContextConstants.TRACE_ID_HEADER, traceId);
        try {
            ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                    .headers(h -> h.add(ContextConstants.TRACE_ID_HEADER, traceId))
                    .build();
            return chain.filter(exchange.mutate().request(serverHttpRequest).build());
        }finally {
            MDC.remove(ContextConstants.TRACE_ID_HEADER);
        }
    }

    @Override
    public int getOrder() {
        return OrderedConstant.TRACE;
    }
}
