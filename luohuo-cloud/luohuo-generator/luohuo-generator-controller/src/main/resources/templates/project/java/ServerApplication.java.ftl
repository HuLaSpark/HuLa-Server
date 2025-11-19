package ${pg.parent};

<#list applicationImport as pkg>
import ${pkg};
</#list>
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import java.net.UnknownHostException;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ${pg.description}启动类
 *
 * @author ${pg.author}
 * @date ${datetime}
 */
@SpringBootApplication
@EnableDiscoveryClient
@Configuration
@EnableFeignClients(value = { "${pg.parent}", "${pg.utilParent}" })
@ComponentScan(basePackages = { "${pg.parent}", "${pg.utilParent}" })
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Slf4j
@EnableFormValidator
public class ${serviceNameUpper}ServerApplication extends ServerApplication {
    public static void main(String[] args) throws UnknownHostException {
        start(${serviceNameUpper}ServerApplication.class, args);
    }
}
