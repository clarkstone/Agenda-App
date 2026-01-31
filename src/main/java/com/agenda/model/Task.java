package com.agenda.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private String id;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private String priority;

    public Task() {
        this.createdAt = LocalDateTime.now();
        this.completed = false;
        this.priority = "MEDIUM";
    }

    public Task(String description, LocalDateTime dueDate) {
        this();
        this.description = description;
        this.dueDate = dueDate;
        this.id = java.util.UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getFormattedDueDate() {
        if (dueDate == null) {
            return "No due date";
        }
        return dueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s", 
            completed ? "✓" : "○", 
            description, 
            getFormattedDueDate());
    }
}
