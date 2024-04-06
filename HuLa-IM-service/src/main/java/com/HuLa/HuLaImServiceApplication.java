package com.HuLa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author nyh
 */
@SpringBootApplication(scanBasePackages = { "com.HuLa" })
public class HuLaImServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuLaImServiceApplication.class, args);
    }

}
