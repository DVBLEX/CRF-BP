package com.crf.server.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.crf.server")
public class CrfWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrfWebApplication.class, args);
    }

}
