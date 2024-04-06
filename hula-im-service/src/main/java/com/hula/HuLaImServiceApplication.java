package com.hula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

/**
 * @author nyh
 */
@SpringBootApplication(scanBasePackages = {"com.hula"})
@MapperScan({"com.hula.common.**.mapper"})
public class HuLaImServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuLaImServiceApplication.class, args);
    }

}
