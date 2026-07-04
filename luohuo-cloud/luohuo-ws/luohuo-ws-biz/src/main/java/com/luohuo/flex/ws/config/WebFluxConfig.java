package com.luohuo.flex.ws.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAutoConfiguration(excludeName = {
        "org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration",
        "org.springframework.boot.webmvc.autoconfigure.DispatcherServletAutoConfiguration"
})
@Import(org.springframework.boot.webflux.autoconfigure.WebFluxAutoConfiguration.class)
public class WebFluxConfig {
}