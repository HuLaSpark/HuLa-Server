package com.luohuo.flex.base.config;

import com.luohuo.flex.im.facade.DefResourceFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.luohuo.basic.boot.config.BaseConfig;
import com.luohuo.basic.constant.Constants;
import com.luohuo.basic.log.event.SysLogListener;
import cn.dev33.satoken.filter.SaTokenContextFilterForJakartaServlet;
import com.luohuo.flex.base.interceptor.AuthenticationSaInterceptor;
import com.luohuo.flex.base.interceptor.TokenContextFilter;
import com.luohuo.flex.common.properties.IgnoreProperties;
import com.luohuo.flex.common.properties.SystemProperties;
import com.luohuo.flex.oauth.facade.LogFacade;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import java.util.EnumSet;

/**
 * 基础服务-Web配置
 *
 * @author 乾乾
 * @date 2021-10-08
 */
@Configuration
@EnableConfigurationProperties({IgnoreProperties.class, SystemProperties.class})
@RequiredArgsConstructor
public class BootWebConfiguration extends BaseConfig implements WebMvcConfigurer {


    private final IgnoreProperties ignoreProperties;
    private final DefResourceFacade defResourceFacade;
    @Value("${spring.profiles.active:dev}")
    protected String profiles;

    @Bean
    public HandlerInterceptor getTokenContextFilter() {
        return new TokenContextFilter(profiles, ignoreProperties);
    }

    @Bean
    public HandlerInterceptor getSaFilter() {
        return new AuthenticationSaInterceptor(ignoreProperties, defResourceFacade);
    }

    /**
     * 注册 SaToken 上下文过滤器，用于初始化 SaToken 上下文
     * 这个过滤器必须在所有其他过滤器之前执行，以确保 SaToken 上下文被正确初始化
     */
	@Bean
	public FilterRegistrationBean saTokenContextFilterForJakartaServlet() {
		FilterRegistrationBean bean = new FilterRegistrationBean<>(new SaTokenContextFilterForJakartaServlet());
		// 配置 Filter 拦截的 URL 模式
		bean.addUrlPatterns("/*");
		// 设置 Filter 的执行顺序,数值越小越先执行
		bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
		bean.setAsyncSupported(true);
		bean.setDispatcherTypes(EnumSet.of(DispatcherType.ASYNC, DispatcherType.REQUEST));
		return bean;
	}

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    /**
     * 注册 拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] commonPathPatterns = getExcludeCommonPathPatterns();
        registry.addInterceptor(getTokenContextFilter())
                .addPathPatterns("/**")
                .order(5)
                .excludePathPatterns(commonPathPatterns);

        // 注册 Sa-Token 拦截器，定义详细认证规则
        registry.addInterceptor(getSaFilter()).addPathPatterns("/**").order(10);


        WebMvcConfigurer.super.addInterceptors(registry);
    }


    /**
     * auth-client 中的拦截器需要排除拦截的地址
     */
    protected String[] getExcludeCommonPathPatterns() {
        return new String[]{
                "/*.css",
                "/*.js",
                "/*.html",
                "/error",
                "/login",
                "/v2/api-docs",
                "/v2/api-docs-ext",
                "/swagger-resources/**",
                "/webjars/**",

                "/",
                "/csrf",

                "/META-INF/resources/**",
                "/resources/**",
                "/static/**",
                "/public/**",
                "classpath:/META-INF/resources/**",
                "classpath:/resources/**",
                "classpath:/static/**",
                "classpath:/public/**",

                "/cache/**",
                "/swagger-ui.html**",
                "/doc.html**"
        };
    }


    /**
     * luohuo.log.enabled = true 并且 luohuo.log.type=DB时实例该类
     */
    @Bean
    @ConditionalOnExpression("${" + Constants.PROJECT_PREFIX + ".log.enabled:true} && 'DB'.equals('${" + Constants.PROJECT_PREFIX + ".log.type:LOGGER}')")
    public SysLogListener sysLogListener(LogFacade logApi) {
        return new SysLogListener(logApi::save);
    }
}
