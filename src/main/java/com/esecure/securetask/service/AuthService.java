package com.esecure.securetask.service;

import com.esecure.securetask.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {
    private final Map<String, String> users = new ConcurrentHashMap<>();
    private final Map<String, String> roles = new ConcurrentHashMap<>();
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService() {
        // demo users
        users.put("admin", encoder.encode("password"));
        roles.put("admin", "ADMIN");

        users.put("alice", encoder.encode("password"));
        roles.put("alice", "USER");
    }

    public boolean verify(String username, String rawPassword) {
        String hashed = users.get(username);
        return hashed != null && encoder.matches(rawPassword, hashed);
    }

    public User find(String username) {
        String role = roles.get(username);
        if (role == null) return null;
        return new User(username, role);
    }
}
