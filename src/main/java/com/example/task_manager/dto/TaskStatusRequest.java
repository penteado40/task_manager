package com.example.task_manager.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskStatusRequest {

    @NotNull
    private Integer statusId;
}