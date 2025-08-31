package com.esecure.securetask.controller;

import com.esecure.securetask.dto.LoginRequest;
import com.esecure.securetask.dto.TokenResponse;
import com.esecure.securetask.model.User;
import com.esecure.securetask.service.AuthService;
import com.esecure.securetask.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwt;

    public AuthController(AuthService authService, @Value("${app.jwt.secret}") String base64Secret) {
        this.authService = authService;
        this.jwt = new JwtUtil(base64Secret, 30 * 60 * 1000);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        boolean ok = authService.verify(req.getUsername(), req.getPassword());
        if (!ok) return ResponseEntity.status(401).body("Invalid credentials");
        User u = authService.find(req.getUsername());
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", u.getRole());
        String token = jwt.generate(claims, u.getUsername());
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
