package com.esecure.securetask.model;

public class Task implements Comparable<Task> {
    private String id;
    private String description;

    public Task(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public int compareTo(Task other) {
        return this.id.compareTo(other.id);
    }
}