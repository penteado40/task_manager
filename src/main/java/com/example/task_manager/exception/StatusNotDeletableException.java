package com.example.task_manager.exception;

public class StatusNotDeletableException extends RuntimeException {
  
    public StatusNotDeletableException(Integer id) {
        super("Status with id " + id + " is a default status and cannot be deleted");
    }
}