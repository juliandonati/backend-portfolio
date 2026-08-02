package com.juliandonati.backendPortafolio.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name="Health",description = "Controlador diseñado para recibir pings y evitar el cold-start")
@RequestMapping("/api/v1/health")
@Controller
public class HealthController {
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {return ResponseEntity.ok("pong");}
}
