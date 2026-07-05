package com.example.task_manager.controller;

import com.example.task_manager.dto.StatusRequest;
import com.example.task_manager.dto.StatusResponse;
import com.example.task_manager.exception.StatusNotFoundException;
import com.example.task_manager.exception.StatusNotDeletableException;
import com.example.task_manager.service.StatusService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatusController.class)
class StatusControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StatusService statusService;

    @Test
    void findAll_returns200WithStatuses() throws Exception {
        when(statusService.findAll()).thenReturn(List.of(new StatusResponse(1, "To Do")));

        mockMvc.perform(get("/statuses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("To Do"));
    }

    @Test
    void findById_returns200_whenExists() throws Exception {
        when(statusService.findById(1)).thenReturn(new StatusResponse(1, "To Do"));

        mockMvc.perform(get("/statuses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("To Do"));
    }

    @Test
    void findById_returns404_whenNotExists() throws Exception {
        when(statusService.findById(99)).thenThrow(new StatusNotFoundException(99));

        mockMvc.perform(get("/statuses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void create_returns201_withValidBody() throws Exception {
        StatusRequest request = new StatusRequest();
        request.setName("In Review");
        when(statusService.create(any())).thenReturn(new StatusResponse(5, "In Review"));

        mockMvc.perform(post("/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("In Review"));
    }

    @Test
    void create_returns400_whenNameIsBlank() throws Exception {
        StatusRequest request = new StatusRequest();
        request.setName("");

        mockMvc.perform(post("/statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void update_returns200_withValidBody() throws Exception {
        StatusRequest request = new StatusRequest();
        request.setName("Backlog");
        when(statusService.update(eq(1), any())).thenReturn(new StatusResponse(1, "Backlog"));

        mockMvc.perform(put("/statuses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Backlog"));
    }

    @Test
    void delete_returns204_whenSuccessful() throws Exception {
        mockMvc.perform(delete("/statuses/1"))
                .andExpect(status().isNoContent());

        verify(statusService).delete(1);
    }

    @Test
    void delete_returns409_whenStatusIsDefault() throws Exception {
        doThrow(new StatusNotDeletableException(1)).when(statusService).delete(1);

        mockMvc.perform(delete("/statuses/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
