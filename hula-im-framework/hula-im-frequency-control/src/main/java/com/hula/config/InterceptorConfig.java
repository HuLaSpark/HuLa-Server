package com.hula.config;

import com.hula.intecepter.CollectorInterceptor;
import com.hula.intecepter.TokenInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置所有拦截器
 * @author nyh
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private TokenInterceptor tokenInterceptor;
    @Resource
    private CollectorInterceptor collectorInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/**");
        registry.addInterceptor(collectorInterceptor)
                .addPathPatterns("/api/**");
    }
}
