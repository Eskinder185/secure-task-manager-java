package com.esecure.securetask.legacy.service;

import model.User;
import utils.JWTUtil;

import java.util.HashMap;

// Simulated authentication service
public class AuthServiceLegacy {
    private static HashMap<String, User> users = new HashMap<>();

    // Hardcoded user data
    static {
        users.put("admin", new User("admin", "admin123", "ADMIN"));
        users.put("user", new User("user", "user123", "USER"));
    }

    // Simulated login method
    public static String login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return JWTUtil.generateToken(user.getUsername(), user.getRole());
        }
        return null;
    }
}
