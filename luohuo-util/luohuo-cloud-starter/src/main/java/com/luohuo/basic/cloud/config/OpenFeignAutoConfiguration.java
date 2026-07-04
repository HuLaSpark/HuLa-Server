package com.luohuo.basic.cloud.config;

import com.luohuo.basic.cloud.feign.DateFormatRegister;
import com.luohuo.basic.cloud.interceptor.FeignAddHeaderRequestInterceptor;
import feign.Feign;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.http.converter.autoconfigure.ClientHttpMessageConvertersCustomizer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.LuohuoFeignClientsRegistrar;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

/**
 * OpenFeign 配置
 *
 * @author 乾乾
 * @date 2019/07/25
 */
@Import(LuohuoFeignClientsRegistrar.class)
@ConditionalOnClass(Feign.class)
@AutoConfigureAfter(EnableFeignClients.class)
public class OpenFeignAutoConfiguration {
    /**
     * 在feign调用方配置， 解决入参和出参是以下类型.
     * 1. @RequestParam("date") Date date
     * 2. @RequestParam("date") LocalDateTime date
     * 3. @RequestParam("date") LocalDate date
     * 4. @RequestParam("date") LocalTime date
     */
    @Bean
    public DateFormatRegister dateFormatRegister() {
        return new DateFormatRegister();
    }

    @Bean
    @ConditionalOnMissingBean
    public FeignHttpMessageConverters feignHttpMessageConverters(
            ObjectProvider<ClientHttpMessageConvertersCustomizer> customizers,
            ObjectProvider<HttpMessageConverterCustomizer> cloudCustomizers) {
        return new FeignHttpMessageConverters(customizers, cloudCustomizers);
    }

    /**
     * feign 支持MultipartFile上传文件
     */
    @Bean
    public Encoder feignFormEncoder(ObjectProvider<FeignHttpMessageConverters> converters) {
        return new SpringEncoder(converters);
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new FeignAddHeaderRequestInterceptor();
    }

    @Bean("restTemplate")
    @Primary
    @ConditionalOnMissingBean(name = "restTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean({"lbRestTemplate", "lbRestTemplateFirst"})
    @LoadBalanced
    @ConditionalOnMissingBean(name = "lbRestTemplate")
    public RestTemplate lbRestTemplate() {
        return new RestTemplate();
    }
}