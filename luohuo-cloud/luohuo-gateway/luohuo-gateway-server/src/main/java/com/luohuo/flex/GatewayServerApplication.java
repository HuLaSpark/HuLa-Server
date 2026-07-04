package com.luohuo.flex;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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
@SpringBootApplication(excludeName = {
        "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
        "org.springframework.boot.web.server.autoconfigure.servlet.ServletWebServerFactoryAutoConfiguration",
        "org.springframework.boot.webmvc.autoconfigure.DispatcherServletAutoConfiguration",
        "org.springframework.boot.websocket.servlet.autoconfigure.WebSocketServletAutoConfiguration"
})
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
