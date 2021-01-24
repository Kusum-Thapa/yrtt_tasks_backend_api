package com.techreturners.model;

public class Task {
    private String taskId;
    private String description;
    private boolean dbsRequired;

    public Task(){}

    public Task(String taskId, String description, boolean dbsRequired) {
        this.taskId = taskId;
        this.description = description;
        this.dbsRequired = dbsRequired;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDbsRequired() {
        return dbsRequired;
    }
}
