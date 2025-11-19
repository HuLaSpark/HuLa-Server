package com.luohuo.flex.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

/**
 * ws消息升级
 *
 * @author 乾乾
 * @date 2025年06月19日10:19:27
 */
@Component
public class WebSocketHeaderFilter extends AbstractGatewayFilterFactory<Object> {

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            // 添加必要的 WebSocket 头信息
            HttpHeaders headers = exchange.getRequest().getHeaders();
            if (isWebSocketRequest(headers)) {
                ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("Connection", "Upgrade")
                    .header("Upgrade", "websocket")
					.header("Sec-WebSocket-Version", "13")
                    .build();

                return chain.filter(exchange.mutate().request(request).build());
            }
            return chain.filter(exchange);
        };
    }

    private boolean isWebSocketRequest(HttpHeaders headers) {
        return "websocket".equalsIgnoreCase(headers.getUpgrade()) ||
               headers.containsKey("Sec-WebSocket-Key");
    }
}
