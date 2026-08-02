package com.juliandonati.backendPortafolio.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name="Health",description = "Controlador diseñado para recibir pings y evitar el cold-start")
public class HealthController {
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {return ResponseEntity.ok("pong");}
}
