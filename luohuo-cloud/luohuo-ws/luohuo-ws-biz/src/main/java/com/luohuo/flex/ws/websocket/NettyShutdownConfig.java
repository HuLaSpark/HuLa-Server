package com.luohuo.flex.ws.websocket;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

/**
 * 优雅停机  --> 清理三套会话系统
 * 功能：Spring上下文关闭时清理所有WebSocket会话
 */
@Slf4j
@Configuration
public class NettyShutdownConfig {
	@Resource
	private SessionManager sessionManager;

	@Bean
	public ApplicationListener<ContextClosedEvent> shutdownListener() {
		return event -> sessionManager.clean();
	}
}