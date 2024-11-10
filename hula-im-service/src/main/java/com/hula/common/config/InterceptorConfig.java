package com.hula.common.config;

import com.hula.common.interceptor.BlackInterceptor;
import com.hula.common.interceptor.CollectorInterceptor;
import com.hula.common.interceptor.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author nyh
 */
@Configuration
@RequiredArgsConstructor
public class InterceptorConfig implements WebMvcConfigurer {

    private final TokenInterceptor tokenInterceptor;
    private final CollectorInterceptor collectorInterceptor;
    private final BlackInterceptor blackInterceptor;

    /** 添加拦截器 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/**");
        registry.addInterceptor(collectorInterceptor)
                .addPathPatterns("/api/**");
        registry.addInterceptor(blackInterceptor)
                .addPathPatterns("/api/**");
    }

}
