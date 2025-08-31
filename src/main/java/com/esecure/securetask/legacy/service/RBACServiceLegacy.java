package com.esecure.securetask.legacy.service;

// Role-Based Access Control (RBAC) logic
public class RBACServiceLegacy {
    // Only ADMINs can do everything; USERs can only read
    public static boolean isAuthorized(String role, String action) {
        if (role.equals("ADMIN")) return true;
        return action.equals("read");
    }
}
