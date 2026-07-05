package com.example.task_manager.controller;

import com.example.task_manager.dto.StatusResponse;
import com.example.task_manager.dto.TaskRequest;
import com.example.task_manager.dto.TaskResponse;
import com.example.task_manager.dto.TaskStatusRequest;
import com.example.task_manager.exception.TaskNotFoundException;
import com.example.task_manager.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    private TaskResponse sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new TaskResponse(1, "Fix bug", null,
                new StatusResponse(1, "To Do"), 3, LocalDateTime.now(), null);
    }

    @Test
    void findAll_returns200() throws Exception {
        when(taskService.findAll(null)).thenReturn(List.of(sampleTask));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Fix bug"));
    }

    @Test
    void findAll_withStatusFilter_returns200() throws Exception {
        when(taskService.findAll(1)).thenReturn(List.of(sampleTask));

        mockMvc.perform(get("/tasks").param("status", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Fix bug"));
    }

    @Test
    void findById_returns200_whenExists() throws Exception {
        when(taskService.findById(1)).thenReturn(sampleTask);

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Fix bug"))
                .andExpect(jsonPath("$.status.name").value("To Do"));
    }

    @Test
    void findById_returns404_whenNotExists() throws Exception {
        when(taskService.findById(99)).thenThrow(new TaskNotFoundException(99));

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void findById_returns400_whenIdIsNotInteger() throws Exception {
        mockMvc.perform(get("/tasks/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid parameter type"));
    }

    @Test
    void create_returns201_withValidBody() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("New task");
        request.setStatusId(1);
        when(taskService.create(any())).thenReturn(sampleTask);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Fix bug"));
    }

    @Test
    void create_returns400_whenTitleIsBlank() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("");
        request.setStatusId(1);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void create_returns400_whenBodyIsMalformed() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed request body"));
    }

    @Test
    void update_returns200_withValidBody() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Updated title");
        request.setStatusId(1);
        when(taskService.update(eq(1), any())).thenReturn(sampleTask);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateStatus_returns200() throws Exception {
        TaskStatusRequest request = new TaskStatusRequest();
        request.setStatusId(2);
        when(taskService.updateStatus(eq(1), any())).thenReturn(sampleTask);

        mockMvc.perform(patch("/tasks/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_returns204() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskService).delete(1);
    }

    @Test
    void delete_returns404_whenNotExists() throws Exception {
        doThrow(new TaskNotFoundException(99)).when(taskService).delete(99);

        mockMvc.perform(delete("/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findByPriority_returns200() throws Exception {
        when(taskService.findByPriority(3, null)).thenReturn(List.of(sampleTask));

        mockMvc.perform(get("/tasks/priority").param("priority", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Fix bug"));
    }

    @Test
    void findByPriority_returns400_whenPriorityIsMissing() throws Exception {
        mockMvc.perform(get("/tasks/priority"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Missing required parameter"));
    }
}
