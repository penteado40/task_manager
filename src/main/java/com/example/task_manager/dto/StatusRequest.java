package com.example.task_manager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StatusRequest {

    @NotBlank
    private String name;
}