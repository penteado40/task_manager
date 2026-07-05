package com.example.task_manager.service;

import com.example.task_manager.dto.TaskRequest;
import com.example.task_manager.dto.TaskResponse;
import com.example.task_manager.dto.TaskStatusRequest;
import com.example.task_manager.dto.StatusResponse;
import com.example.task_manager.entity.Status;
import com.example.task_manager.entity.Task;
import com.example.task_manager.exception.StatusNotFoundException;
import com.example.task_manager.exception.TaskNotFoundException;
import com.example.task_manager.repository.StatusRepository;
import com.example.task_manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
  @RequiredArgsConstructor
  public class TaskService {

      private final TaskRepository taskRepository;
      private final StatusRepository statusRepository;

      public List<TaskResponse> findAll(Integer statusId) {
          if (statusId != null) {
              Status status = getStatusOrThrow(statusId);
              return taskRepository.findByStatus(status).stream()
                      .map(this::toResponse)
                      .toList();
          }
          return taskRepository.findAll().stream()
                  .map(this::toResponse)
                  .toList();
      }

      public List<TaskResponse> findByPriority(Integer priority, Integer statusId) {
          if (statusId != null) {
              Status status = getStatusOrThrow(statusId);
              return taskRepository.findByPriorityAndStatus(priority, status).stream()
                      .map(this::toResponse)
                      .toList();
          }
          return taskRepository.findByPriority(priority).stream()
                  .map(this::toResponse)
                  .toList();
      }

      public TaskResponse findById(Integer id) {
          return toResponse(getTaskOrThrow(id));
      }

      public TaskResponse create(TaskRequest request) {
        if(request.getStatusId() == null) {
            Status status = getStatusOrThrow(1);
            request.setStatusId(status.getId());
        }
        if(request.getPriority() == null) {
            request.setPriority(1);
        }

          Status status = getStatusOrThrow(request.getStatusId());
          Task task = Task.builder()
                  .title(request.getTitle())
                  .description(request.getDescription())
                  .status(status)
                  .priority(request.getPriority())
                  .build();
          return toResponse(taskRepository.save(task));
      }

      public TaskResponse update(Integer id, TaskRequest request) {
          Task task = getTaskOrThrow(id);
          Status status = getStatusOrThrow(request.getStatusId());
          task.setTitle(request.getTitle());
          task.setDescription(request.getDescription());
          task.setStatus(status);
          task.setPriority(request.getPriority());
          return toResponse(taskRepository.save(task));
      }

      public TaskResponse updateStatus(Integer id, TaskStatusRequest request) {
          Task task = getTaskOrThrow(id);
          Status status = getStatusOrThrow(request.getStatusId());
          task.setStatus(status);
          return toResponse(taskRepository.save(task));
      }

      public void delete(Integer id) {
          Task task = getTaskOrThrow(id);
          taskRepository.delete(task);
      }

      private Task getTaskOrThrow(Integer id) {
          return taskRepository.findById(id)
                  .orElseThrow(() -> new TaskNotFoundException(id));
      }

      private Status getStatusOrThrow(Integer id) {
          return statusRepository.findById(id)
                  .orElseThrow(() -> new StatusNotFoundException(id));
      }

      private TaskResponse toResponse(Task task) {
          return new TaskResponse(
                  task.getId(),
                  task.getTitle(),
                  task.getDescription(),
                  new StatusResponse(task.getStatus().getId(), task.getStatus().getName()),
                  task.getPriority(),
                  task.getCreatedAt(),
                  task.getUpdatedAt()
          );
      }
  }