package com.luohuo.flex;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author 乾乾
 * @date 2018-01-14 11:11
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAdminServer
@Slf4j
public class MonitorServerApplication {
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(MonitorServerApplication.class, args);
        Environment env = application.getEnvironment();
        String msg = """
                                                                                 
                ----------------------------------------------------------
                应用 '{}' 启动成功!
                监控地址: http://{}:{}{}
                ----------------------------------------------------------
                """;
        log.info(msg,
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                env.getProperty("server.servlet.context-path", "")
        );
    }
}
