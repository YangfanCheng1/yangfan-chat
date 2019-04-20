package com.yangfan.chat.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheck {

    private String version;
    HealthCheck(@Value("${project.version:1.0}") final String version) {
        this.version = version;
    }

    @GetMapping(value = "/health", produces = {"application/json"})
    public String healthCheck() {
        return "{\"version\":\"" + version + "\"}";
    }

}


