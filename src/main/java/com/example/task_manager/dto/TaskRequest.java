package com.example.task_manager.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRequest {

    @NotBlank
    private String title;

    private String description;

    // @NotNull
    private Integer statusId;

    @Min(1)
    @Max(5)
    private Integer priority;
}