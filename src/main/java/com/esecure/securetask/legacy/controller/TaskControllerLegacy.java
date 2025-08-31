package com.esecure.securetask.legacy.controller;

import service.TaskService;
import service.RBACService;
import model.Task;
import utils.JWTUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TaskControllerLegacy {

    public static void handleRequest(String token, String action, Task task) {
        if (!JWTUtil.validateToken(token)) {
            log("Invalid token - Access denied");
            return;
        }

        String role = JWTUtil.extractRole(token);
        if (!RBACService.isAuthorized(role, action)) {
            log("Unauthorized action by role: " + role);
            return;
        }

        switch (action) {
            case "create":
                TaskService.addTask(task);
                log("Task created: " + task.getId());
                break;
            case "read":
                TaskService.getAllTasks().forEach(t ->
                    System.out.println(t.getId() + " - " + t.getDescription()));
                break;
            case "update":
                TaskService.updateTask(task.getId(), task.getDescription());
                log("Task updated: " + task.getId());
                break;
            case "delete":
                TaskService.deleteTask(task.getId());
                log("Task deleted: " + task.getId());
                break;
        }
    }

    private static void log(String message) {
        try {
            Files.createDirectories(Paths.get("logs"));

            // Write log with timestamp
            try (FileWriter writer = new FileWriter("logs/access.log", true)) {
                String timestamp = LocalDateTime.now().toString();
                writer.write(timestamp + " - " + message + "\n");
            }
        } catch (IOException e) {
            System.out.println("Log error: " + e.getMessage());
        }
    }
}

