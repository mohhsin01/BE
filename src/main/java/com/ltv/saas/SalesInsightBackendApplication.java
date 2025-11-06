package com.ltv.saas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SalesInsightBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalesInsightBackendApplication.class, args);
    }
}
