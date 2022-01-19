package com.crf.server.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "com.crf.server")
public class CrfRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrfRestApplication.class, args);
    }
}
