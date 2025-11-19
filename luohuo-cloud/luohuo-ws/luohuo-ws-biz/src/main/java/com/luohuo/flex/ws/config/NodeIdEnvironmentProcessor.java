package com.luohuo.flex.ws.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * 节点生成器，促使每个ws服务的节点名称都不一样
 */
public class NodeIdEnvironmentProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication app) {
        String nodeId = "ws" + new SecureRandom().ints(6, 0, 16)
                .mapToObj(i -> Integer.toHexString(i))
                .collect(Collectors.joining());
        env.getPropertySources().addFirst(
            new MapPropertySource("customNodeId", Collections.singletonMap("luohuo.node-id", nodeId))
        );
    }
}