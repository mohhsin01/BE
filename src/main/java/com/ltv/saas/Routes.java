package com.ltv.saas;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Routes {

    @GetMapping("/")
    public String home() {
        return "Sales Insight Backend is up and running!";
    }

    @GetMapping("/health")
    public String health() {
        return "Server status: OK";
    }
}