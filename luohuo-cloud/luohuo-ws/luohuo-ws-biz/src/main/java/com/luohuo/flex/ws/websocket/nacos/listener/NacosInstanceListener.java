package com.luohuo.flex.ws.websocket.nacos.listener;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.luohuo.basic.mq.redis.core.RedisMQTemplate;
import com.luohuo.flex.ws.websocket.entity.NodeDownMessage;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Nacos实例变化监听器
 *
 * 监听Nacos服务实例变化，处理节点上下线事件
 */
@Slf4j
@Component
public class NacosInstanceListener {
    private final NamingService namingService;
	private final RedisMQTemplate redisMQTemplate;
	private final String nodeId;

	@Autowired
	public NacosInstanceListener(
			NacosServiceManager nacosServiceManager,
			NacosDiscoveryProperties discoveryProperties,
			@Value("${luohuo.node-id}") String nodeId,
			RedisMQTemplate redisMQTemplate
	) {
		this.nodeId = nodeId;
		this.namingService = nacosServiceManager.getNamingService(discoveryProperties.getNacosProperties());
		this.redisMQTemplate = redisMQTemplate;
	}


	@PostConstruct
    public void init() {
        // 订阅服务变更
        try {
            namingService.subscribe("ws-cluster", "WS_GROUP", event -> {
                if (event instanceof NamingEvent) {
                    handleInstanceChange(((NamingEvent) event).getInstances());
                }
            });
        } catch (NacosException e) {
            log.error("Nacos订阅失败", e);
        }
    }

	/**
	 * 处理服务实例变化事件
	 * 检测下线节点，并发送节点下线通知。
	 * @param instances 所有实例
	 */
	private void handleInstanceChange(List<Instance> instances) {
		// 检测下线节点
		Set<String> activeNodes = instances.stream()
				.filter(Instance::isHealthy)
				.map(i -> i.getMetadata().get("nodeId"))
				.collect(Collectors.toSet());

		Set<String> allNodes = instances.stream()
				.map(i -> i.getMetadata().get("nodeId"))
				.collect(Collectors.toSet());

		allNodes.removeAll(activeNodes);
		allNodes.stream()
				.filter(id -> !id.equals(this.nodeId)).forEach(nodeId -> {
			redisMQTemplate.send(new NodeDownMessage(nodeId));
			log.warn("节点下线通知已发送: {}", nodeId);
		});
	}

}