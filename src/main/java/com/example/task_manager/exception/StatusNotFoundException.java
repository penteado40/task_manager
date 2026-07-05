package com.example.task_manager.exception;

public class StatusNotFoundException extends RuntimeException {

    public StatusNotFoundException(Integer id) {
        super("No status with id " + id);
    }
}
