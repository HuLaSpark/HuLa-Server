package com.luohuo.flex.ws.websocket.processor;

import cn.hutool.json.JSONUtil;
import com.luohuo.flex.model.ws.WSBaseReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;

import java.util.List;

/**
 * 责任链调度器
 * 1. 自动注入所有MessageProcessor实现
 * 2. 利用spring机制，按@Order顺序执行首个匹配的处理器
 * 3. 全局异常捕获避免链断裂
 */
@Slf4j
@Service
public class MessageHandlerChain {
  private final List<MessageProcessor> processors;

	public MessageHandlerChain(List<MessageProcessor> processors) {
		this.processors = processors;
	}

	public void handleMessage(WebSocketSession session, Long uid, String payload) {
		WSBaseReq bean = JSONUtil.toBean(payload, WSBaseReq.class);
		processors.stream()
				.filter(p -> {
					try {
						return p.supports(bean);
					} catch (Exception e) {
						log.error("处理器[{}]支持检查异常", p.getClass().getSimpleName(), e);
						return false;
					}
				})
				.findFirst()
				.ifPresent(p -> {
					try {
						p.process(session, uid, bean);
					} catch (Exception e) {
						log.error("处理器[{}]执行失败", p.getClass().getSimpleName(), e);
					}
				});
	}
}
