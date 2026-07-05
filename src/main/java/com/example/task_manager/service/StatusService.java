package com.example.task_manager.service;

import com.example.task_manager.dto.StatusRequest;
import com.example.task_manager.dto.StatusResponse;
import com.example.task_manager.entity.Status;
import com.example.task_manager.exception.StatusNotFoundException;
import com.example.task_manager.exception.StatusNotDeletableException;
import com.example.task_manager.repository.StatusRepository;
import com.example.task_manager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final TaskRepository taskRepository;

    public List<StatusResponse> findAll() {
        return statusRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public StatusResponse findById(Integer id) {
        return toResponse(getOrThrow(id));
    }

    public StatusResponse create(StatusRequest request) {
        Status status = Status.builder()
                .name(request.getName())
                .isDefault(false)
                .build();
        return toResponse(statusRepository.save(status));
    }

    public StatusResponse update(Integer id, StatusRequest request) {
        Status status = getOrThrow(id);
        status.setName(request.getName());
        return toResponse(statusRepository.save(status));
    }

    @Transactional
    public void delete(Integer id) {
        Status status = getOrThrow(id);

        if (status.isDefault()) {
            throw new StatusNotDeletableException(id);
        }

        Status fallback = statusRepository.findByIsDefaultTrue().getFirst();

        taskRepository.findByStatus(status)
                .forEach(task -> {
                    task.setStatus(fallback);
                    taskRepository.save(task);
                });

        statusRepository.delete(status);
    }

    private Status getOrThrow(Integer id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new StatusNotFoundException(id));
    }

    private StatusResponse toResponse(Status status) {
        return new StatusResponse(status.getId(), status.getName());
    }
}