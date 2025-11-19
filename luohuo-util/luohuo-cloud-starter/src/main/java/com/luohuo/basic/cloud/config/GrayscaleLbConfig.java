package com.luohuo.basic.cloud.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import com.luohuo.basic.cloud.rule.GrayscaleVersionRoundRobinLoadBalancer;
import com.luohuo.basic.utils.StrPool;

/**
 * 灰度配置
 *
 * @author tangyh
 * @version v1.0
 * @date 2021/7/13 11:36 上午
 * @create [2021/7/13 11:36 上午 ] [tangyh] [初始创建]
 */
public class GrayscaleLbConfig {

    @Bean
    public ReactorLoadBalancer<ServiceInstance> reactorServiceInstanceLoadBalancer(Environment environment,
                                                                                   LoadBalancerClientFactory loadBalancerClientFactory) {
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME, StrPool.EMPTY);
        return new GrayscaleVersionRoundRobinLoadBalancer(loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
    }
}
