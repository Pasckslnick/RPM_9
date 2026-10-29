package com.example.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/car")
    public ResponseEntity<Map<String, String>> carFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "service", "car-service",
                        "status", "unavailable",
                        "message", "Сервис автомобилей временно недоступен. Попробуйте позже."
                ));
    }

    @GetMapping("/fallback/user")
    public ResponseEntity<Map<String, String>> userFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "service", "user-service",
                        "status", "unavailable",
                        "message", "Сервис пользователей временно недоступен. Попробуйте позже."
                ));
    }

    @GetMapping("/fallback/test-drive")
    public ResponseEntity<Map<String, String>> testDriveFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "service", "test-drives-service",
                        "status", "unavailable",
                        "message", "Сервис тест-драйвов временно недоступен. Попробуйте позже."
                ));
    }

    @GetMapping("/fallback/news")
    public ResponseEntity<Map<String, String>> newsFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "service", "news-service",
                        "status", "unavailable",
                        "message", "Сервис новостей временно недоступен. Попробуйте позже."
                ));
    }
}
