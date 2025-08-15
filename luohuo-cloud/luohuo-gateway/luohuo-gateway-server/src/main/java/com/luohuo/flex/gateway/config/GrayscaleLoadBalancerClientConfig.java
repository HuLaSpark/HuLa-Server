package com.luohuo.flex.gateway.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.config.GatewayLoadBalancerProperties;
import org.springframework.cloud.gateway.config.GatewayReactiveLoadBalancerClientAutoConfiguration;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.luohuo.basic.constant.Constants;
import com.luohuo.flex.gateway.filter.GrayscaleReactiveLoadBalancerClientFilter;
import com.luohuo.flex.gateway.rule.GrayVersionLoadBalancer;
import com.luohuo.flex.gateway.rule.GrayscaleLoadBalancer;

/**
 * 灰度负载模式自动装配
 *
 * @author zuihou
 * @date 2021年07月13日08:37:32
 */
@Configuration
@EnableConfigurationProperties(GatewayLoadBalancerProperties.class)
@ConditionalOnProperty(value = Constants.PROJECT_PREFIX + ".grayscale.enabled", havingValue = "true", matchIfMissing = true)
@AutoConfigureBefore(GatewayReactiveLoadBalancerClientAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class GrayscaleLoadBalancerClientConfig {

    @Bean
    public GrayscaleLoadBalancer grayLoadBalancer(DiscoveryClient discoveryClient) {
        return new GrayVersionLoadBalancer(discoveryClient);
    }

    @Bean
    public ReactiveLoadBalancerClientFilter gatewayLoadBalancerClientFilter(GrayscaleLoadBalancer grayLoadBalancer,
                                                                            GatewayLoadBalancerProperties properties) {
        return new GrayscaleReactiveLoadBalancerClientFilter(properties, grayLoadBalancer);
    }

}
