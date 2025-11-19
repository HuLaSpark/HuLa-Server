package com.luohuo.flex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import com.luohuo.flex.common.ServerApplication;

import java.net.UnknownHostException;

import static com.luohuo.flex.common.constant.BizConstant.BUSINESS_PACKAGE;
import static com.luohuo.flex.common.constant.BizConstant.UTIL_PACKAGE;

/**
 * @author 乾乾
 * @date 2017-12-13 15:02
 */
@SpringBootApplication(exclude = {
		DataSourceAutoConfiguration.class,
		org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration.class,
		org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration.class,
		org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration.class,
		// 排除Tomcat和Undertow的自动配置
		org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration.class})
@EnableDiscoveryClient
@ComponentScan({
        UTIL_PACKAGE, BUSINESS_PACKAGE
})
@EnableFeignClients(value = {
        UTIL_PACKAGE, BUSINESS_PACKAGE
})
@Slf4j
public class GatewayServerApplication extends ServerApplication {
    public static void main(String[] args) throws UnknownHostException {
        start(GatewayServerApplication.class, args);
    }

}
