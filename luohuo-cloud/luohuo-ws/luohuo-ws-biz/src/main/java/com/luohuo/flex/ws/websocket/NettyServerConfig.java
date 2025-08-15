package com.luohuo.flex.ws.websocket;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import java.time.Duration;
import java.util.Map;

/**
 * netty + WebFlux 混合配置
 * 功能：配置WebSocket路由、Netty参数调优、Servlet容器兼容
 * @author 乾乾
 * @date 2025-06-08
 */
@Slf4j
@Configuration
public class NettyServerConfig {

	private  ReactiveWebSocketHandler webSocketHandler;

	public NettyServerConfig(ReactiveWebSocketHandler webSocketHandler) {
		this.webSocketHandler = webSocketHandler;
	}

	@Bean
	public WebSocketHandlerAdapter webSocketHandlerAdapter() {
		return new WebSocketHandlerAdapter();
	}

	/**
	 * 消息进来之后直接转发给响应式消息处理器
	 * @return
	 */
	@Bean
	public WebSocketHandler messageWebSocketHandler() {
		return session -> webSocketHandler.handle(session);
	}

	@Bean
	public HandlerMapping webSocketMapping() {
		// 配置连接地址
		Map<String, WebSocketHandler> map = Map.of("/ws", messageWebSocketHandler());
		SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
		handlerMapping.setUrlMap(map);
		handlerMapping.setOrder(-1); // 最高优先级
		return handlerMapping;
	}

	// 添加对 Servlet 网关的支持
	@Bean
	public ServletWebSocketHandlerAdapter servletAdapter() {
		return new ServletWebSocketHandlerAdapter();
	}

	static class ServletWebSocketHandlerAdapter extends WebSocketHandlerAdapter {
		// 空实现，仅用于兼容 Servlet API
	}

	/**
	 * netty 配置调优
	 * @return
	 */
	@Bean
	public NettyReactiveWebServerFactory nettyReactiveWebServerFactory() {
		NettyReactiveWebServerFactory factory = new NettyReactiveWebServerFactory();
		factory.addServerCustomizers(httpServer -> httpServer
				.option(ChannelOption.SO_BACKLOG, 2048) // 连接队列
				.idleTimeout(Duration.ofMinutes(10)) // 10分钟没有任何读写主动关闭链接
				.childOption(ChannelOption.SO_RCVBUF, 4 * 1024)
				.childOption(ChannelOption.SO_SNDBUF, 4 * 1024));
		return factory;
	}

}