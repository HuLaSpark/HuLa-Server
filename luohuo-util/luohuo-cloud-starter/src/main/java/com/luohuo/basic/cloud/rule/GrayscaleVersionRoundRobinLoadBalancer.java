package com.luohuo.basic.cloud.rule;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.nacos.NacosServiceInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.DefaultResponse;
import org.springframework.cloud.client.loadbalancer.EmptyResponse;
import org.springframework.cloud.client.loadbalancer.Request;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import reactor.core.publisher.Mono;
import com.luohuo.basic.context.ContextConstants;
import com.luohuo.basic.context.ContextUtil;

import java.util.List;
import java.util.Map;


/**
 * 灰度负载均衡
 *
 * @author tangyh
 * @version v1.0
 * @date 2021/7/12 9:22 下午
 * @create [2021/7/12 9:22 下午 ] [tangyh] [初始创建]
 */
@Slf4j
public class GrayscaleVersionRoundRobinLoadBalancer extends RoundRobinLoadBalancer {
    private ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;

    private String serviceId;

    public GrayscaleVersionRoundRobinLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId) {
        super(serviceInstanceListSupplierProvider, serviceId);
        this.serviceId = serviceId;
        this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    }

    public GrayscaleVersionRoundRobinLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId, int seedPosition) {
        super(serviceInstanceListSupplierProvider, serviceId, seedPosition);
    }

    @Override
    public Mono<Response<ServiceInstance>> choose(Request request) {
        ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
                .getIfAvailable(NoopServiceInstanceListSupplier::new);
        return supplier.get(request).next().map(serviceInstances -> getInstanceResponse(serviceInstances, request));
    }

    private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> instances, Request request) {
        if (CollUtil.isEmpty(instances)) {
            log.warn("注册中心无可用实例 {}", serviceId);
            return new EmptyResponse();
        }
        String grayVersion = ContextUtil.getGrayVersion();

        if (StrUtil.isBlank(grayVersion)) {
            return super.choose(request).block();
        }

        for (ServiceInstance instance : instances) {
            NacosServiceInstance nacosInstance = (NacosServiceInstance) instance;
            Map<String, String> metadata = nacosInstance.getMetadata();
            if (grayVersion.equalsIgnoreCase(metadata.get(ContextConstants.GRAY_VERSION))) {
                log.debug("灰度 匹配成功， 参数：{} 实例：{}", grayVersion, nacosInstance);
                return new DefaultResponse(nacosInstance);
            }
        }
        return super.choose(request).block();
    }
}
