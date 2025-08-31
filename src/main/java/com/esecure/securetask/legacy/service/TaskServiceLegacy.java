package com.esecure.securetask.legacy.service;

import java.util.ArrayList;
import java.util.List;
import model.AVLTree;
import model.Task;

public class TaskServiceLegacy {
    private static AVLTree<Task> taskTree = new AVLTree<>();
    private static List<Task> taskList = new ArrayList<>();

    // Return all tasks
    public static List<Task> getAllTasks() {
        return taskList;
    }

    // Add a task
    public static void addTask(Task task) {
        taskTree.insert(task);
        taskList.add(task);
    }

    // Update task by ID
    public static boolean updateTask(String id, String newDesc) {
        Task task = getTaskById(id);
        if (task != null) {
            task.setDescription(newDesc);
            return true;
        }
        return false;
    }

    // Delete task by ID
    public static boolean deleteTask(String id) {
        Task task = getTaskById(id);
        if (task != null) {
            taskTree.delete(task);
            taskList.remove(task);
            return true;
        }
        return false;
    }

    // Fetch task by ID
    public static Task getTaskById(String id) {
        for (Task task : taskList) {
            if (task.getId().equals(id)) return task;
        }
        return null;
    }

    // Check if task exists
    public static boolean containsTask(String id) {
        return getTaskById(id) != null;
    }
}