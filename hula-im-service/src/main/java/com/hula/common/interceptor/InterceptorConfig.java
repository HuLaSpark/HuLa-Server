package com.hula.common.interceptor;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author nyh
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private TokenInterceptor tokenInterceptor;
    @Resource
    private CollectorInterceptor collectorInterceptor;

    /** 添加拦截器 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/**");
        registry.addInterceptor(collectorInterceptor)
                .addPathPatterns("/**");
    }
}
