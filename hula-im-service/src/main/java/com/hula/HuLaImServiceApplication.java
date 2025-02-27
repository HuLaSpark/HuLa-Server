package com.hula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author nyh
 */
@SpringBootApplication(scanBasePackages = {"com.hula"})
@ServletComponentScan
public class HuLaImServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuLaImServiceApplication.class, args);
    }

}
