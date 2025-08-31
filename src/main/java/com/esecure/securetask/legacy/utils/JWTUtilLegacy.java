package com.esecure.securetask.legacy.utils;

// Simulated JWT token generator and validator
public class JWTUtilLegacy {
    // Create a token using username and role
    public static String generateToken(String username, String role) {
        return username + ":" + role + ":securetoken";
    }

    // Validate token format
    public static boolean validateToken(String token) {
        return token != null && token.contains("securetoken");
    }

    // Extract the user role from token
    public static String extractRole(String token) {
        return token.split(":")[1];
    }
}
