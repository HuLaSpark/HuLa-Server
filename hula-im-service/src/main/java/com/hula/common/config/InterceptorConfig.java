package com.hula.common.config;

import com.hula.common.interceptor.BlackInterceptor;
import com.hula.common.interceptor.CollectorInterceptor;
import com.hula.common.interceptor.TokenInterceptor;
import com.hula.core.user.service.cache.UserCache;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
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

    @Resource
    private UserCache userCache;

    /** 添加拦截器 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/**");
        registry.addInterceptor(collectorInterceptor)
                .addPathPatterns("/api/**");
        registry.addInterceptor(blackInterceptor(userCache))
                .addPathPatterns("/api/**");
    }

    @Bean
    public BlackInterceptor blackInterceptor(UserCache userCache) {
        return new BlackInterceptor(userCache);
    }
}
