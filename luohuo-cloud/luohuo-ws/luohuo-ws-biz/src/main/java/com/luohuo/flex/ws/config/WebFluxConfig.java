package com.luohuo.flex.ws.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration(exclude = {
		WebMvcAutoConfiguration.class,  // 禁用 MVC 自动配置
		org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration.class
})
@Import(org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration.class)
public class WebFluxConfig {
}