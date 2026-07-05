package com.example.task_manager.controller;

import com.example.task_manager.dto.StatusRequest;
import com.example.task_manager.dto.StatusResponse;
import com.example.task_manager.service.StatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statuses")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

      @PostMapping
      public ResponseEntity<StatusResponse> create(@RequestBody @Valid StatusRequest request) {
          return ResponseEntity.status(HttpStatus.CREATED).body(statusService.create(request));
      }

      @PutMapping("/{id}")
      public StatusResponse update(@PathVariable Integer id, @RequestBody @Valid StatusRequest request) {
          return statusService.update(id, request);
      }

      @DeleteMapping("/{id}")
      public ResponseEntity<Void> delete(@PathVariable Integer id) {
          statusService.delete(id);
          return ResponseEntity.noContent().build();
      }
  }