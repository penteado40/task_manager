package com.example.task_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TaskResponse {

    private Integer id;
    private String title;
    private String description;
    private StatusResponse status;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}