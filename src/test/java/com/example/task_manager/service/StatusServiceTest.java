package com.example.task_manager.service;

import com.example.task_manager.dto.StatusRequest;
import com.example.task_manager.dto.StatusResponse;
import com.example.task_manager.entity.Status;
import com.example.task_manager.entity.Task;
import com.example.task_manager.exception.StatusNotFoundException;
import com.example.task_manager.exception.StatusNotDeletableException;
import com.example.task_manager.repository.StatusRepository;
import com.example.task_manager.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusServiceTest {

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private StatusService statusService;

    @Test
    void findAll_returnsAllStatuses() {
        Status s1 = Status.builder().id(1).name("To Do").isDefault(true).build();
        Status s2 = Status.builder().id(2).name("Done").isDefault(true).build();
        when(statusRepository.findAll()).thenReturn(List.of(s1, s2));

        List<StatusResponse> result = statusService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("To Do");
    }

    @Test
    void findById_returnsStatus_whenExists() {
        Status status = Status.builder().id(1).name("To Do").isDefault(true).build();
        when(statusRepository.findById(1)).thenReturn(Optional.of(status));

        StatusResponse result = statusService.findById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getName()).isEqualTo("To Do");
    }

    @Test
    void findById_throwsStatusNotFoundException_whenNotExists() {
        when(statusRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> statusService.findById(99))
                .isInstanceOf(StatusNotFoundException.class);
    }

    @Test
    void create_savesStatusWithIsDefaultFalse() {
        StatusRequest request = new StatusRequest();
        request.setName("In Review");
        Status saved = Status.builder().id(5).name("In Review").isDefault(false).build();
        when(statusRepository.save(any(Status.class))).thenReturn(saved);

        StatusResponse result = statusService.create(request);

        assertThat(result.getName()).isEqualTo("In Review");
        verify(statusRepository).save(argThat(s -> !s.isDefault()));
    }

    @Test
    void update_updatesName_whenExists() {
        Status status = Status.builder().id(1).name("To Do").isDefault(true).build();
        StatusRequest request = new StatusRequest();
        request.setName("Backlog");
        when(statusRepository.findById(1)).thenReturn(Optional.of(status));
        when(statusRepository.save(any())).thenReturn(Status.builder().id(1).name("Backlog").isDefault(true).build());

        StatusResponse result = statusService.update(1, request);

        assertThat(result.getName()).isEqualTo("Backlog");
    }

    @Test
    void update_throwsStatusNotFoundException_whenNotExists() {
        when(statusRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> statusService.update(99, new StatusRequest()))
                .isInstanceOf(StatusNotFoundException.class);
    }

    @Test
    void delete_throwsStatusNotDeletableException_whenDefault() {
        Status status = Status.builder().id(1).name("To Do").isDefault(true).build();
        when(statusRepository.findById(1)).thenReturn(Optional.of(status));

        assertThatThrownBy(() -> statusService.delete(1))
                .isInstanceOf(StatusNotDeletableException.class);

        verify(statusRepository, never()).delete(any());
    }

    @Test
    void delete_reassignsTasksAndDeletes_whenNotDefault() {
        Status fallback = Status.builder().id(1).name("To Do").isDefault(true).build();
        Status toDelete = Status.builder().id(5).name("In Review").isDefault(false).build();
        Task task = Task.builder().id(1).title("Fix bug").status(toDelete).build();

        when(statusRepository.findById(5)).thenReturn(Optional.of(toDelete));
        when(statusRepository.findByIsDefaultTrue()).thenReturn(List.of(fallback));
        when(taskRepository.findByStatus(toDelete)).thenReturn(List.of(task));

        statusService.delete(5);

        assertThat(task.getStatus()).isEqualTo(fallback);
        verify(taskRepository).save(task);
        verify(statusRepository).delete(toDelete);
    }

    @Test
    void delete_throwsStatusNotFoundException_whenNotExists() {
        when(statusRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> statusService.delete(99))
                .isInstanceOf(StatusNotFoundException.class);
    }
}
