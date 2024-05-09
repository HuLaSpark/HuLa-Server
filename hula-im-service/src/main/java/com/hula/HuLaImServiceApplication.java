package com.hula;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author nyh
 */
@MapperScan({"com.hula.**.mapper"})
@SpringBootApplication(scanBasePackages = {"com.hula"})
@ServletComponentScan
public class HuLaImServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuLaImServiceApplication.class, args);
    }

}
