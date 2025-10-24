package com.AdaptiveInterviewSimulator.web;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {
    @GetMapping("/api/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok().body(Map.of("status", "UP", "app", "adaptive-interview-simulator"));
    }
}