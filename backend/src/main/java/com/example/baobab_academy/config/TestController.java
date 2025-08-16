package com.example.baobab_academy.config;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    @GetMapping
    public ResponseEntity<String> test() {
        log.info("🧪 Test endpoint GET appelé");
        return ResponseEntity.ok("API Test GET fonctionne!");
    }

    @PostMapping
    public ResponseEntity<String> testPost(@RequestBody(required = false) String data) {
        log.info("🧪 Test endpoint POST appelé avec: {}", data);
        return ResponseEntity.ok("API Test POST fonctionne! Données reçues: " + data);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> testAdmin() {
        log.info("🧪 Test admin endpoint appelé");
        return ResponseEntity.ok("Test admin fonctionne!");
    }
}