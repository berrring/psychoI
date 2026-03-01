package com.example.psycho.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SystemController {

    @GetMapping("/")
    public Map<String, String> root() {
        return Map.of(
                "service", "psycho-backend",
                "status", "ok",
                "apiBase", "/api/v1",
                "docs", "/swagger-ui.html"
        );
    }

    @GetMapping("/healthz")
    public Map<String, String> health() {
        return Map.of("status", "ok");
    }
}
