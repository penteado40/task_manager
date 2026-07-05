package com.example.task_manager.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Integer id) {
        super("No task with id " + id);
    }
}