package com.luohuo.basic.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import com.luohuo.basic.mq.properties.MqProperties;

@Configuration
@Import(EarthRocketMqConfiguration.RocketMqConfiguration.class)
@EnableConfigurationProperties(MqProperties.class)
public class EarthRocketMqConfiguration {

    @Slf4j
    @Configuration
    @ConditionalOnProperty(prefix = MqProperties.PREFIX, name = "enabled", havingValue = "false", matchIfMissing = true)
    @EnableAutoConfiguration(exclude = {RocketMQAutoConfiguration.class})
    public static class RocketMqConfiguration {
        public RocketMqConfiguration() {
            log.warn("检测到 mq.rocket.enabled=false，排除了 RocketMQ");
        }
    }
}