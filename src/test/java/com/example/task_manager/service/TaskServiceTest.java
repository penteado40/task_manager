package com.example.task_manager.service;

import com.example.task_manager.dto.TaskRequest;
import com.example.task_manager.dto.TaskResponse;
import com.example.task_manager.dto.TaskStatusRequest;
import com.example.task_manager.entity.Status;
import com.example.task_manager.entity.Task;
import com.example.task_manager.exception.StatusNotFoundException;
import com.example.task_manager.exception.TaskNotFoundException;
import com.example.task_manager.repository.StatusRepository;
import com.example.task_manager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
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
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private TaskService taskService;

    private Status todoStatus;
    private Task sampleTask;

    @BeforeEach
    void setUp() {
        todoStatus = Status.builder().id(1).name("To Do").isDefault(true).build();
        sampleTask = Task.builder().id(1).title("Fix bug").status(todoStatus).priority(3).build();
    }

    @Test
    void findAll_withNoFilter_returnsAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(sampleTask));

        List<TaskResponse> result = taskService.findAll(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Fix bug");
    }

    @Test
    void findAll_withStatusFilter_returnsFilteredTasks() {
        when(statusRepository.findById(1)).thenReturn(Optional.of(todoStatus));
        when(taskRepository.findByStatus(todoStatus)).thenReturn(List.of(sampleTask));

        List<TaskResponse> result = taskService.findAll(1);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus().getId()).isEqualTo(1);
    }

    @Test
    void findAll_withStatusFilter_throwsStatusNotFoundException_whenStatusNotExists() {
        when(statusRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findAll(99))
                .isInstanceOf(StatusNotFoundException.class);
    }

    @Test
    void findById_returnsTask_whenExists() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(sampleTask));

        TaskResponse result = taskService.findById(1);

        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getTitle()).isEqualTo("Fix bug");
    }

    @Test
    void findById_throwsTaskNotFoundException_whenNotExists() {
        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(99))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void findByPriority_withNoStatusFilter_returnsTasksByPriority() {
        when(taskRepository.findByPriority(3)).thenReturn(List.of(sampleTask));

        List<TaskResponse> result = taskService.findByPriority(3, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPriority()).isEqualTo(3);
    }

    @Test
    void findByPriority_withStatusFilter_returnsFilteredTasks() {
        when(statusRepository.findById(1)).thenReturn(Optional.of(todoStatus));
        when(taskRepository.findByPriorityAndStatus(3, todoStatus)).thenReturn(List.of(sampleTask));

        List<TaskResponse> result = taskService.findByPriority(3, 1);

        assertThat(result).hasSize(1);
    }

    @Test
    void create_savesTask() {
        TaskRequest request = new TaskRequest();
        request.setTitle("New task");
        request.setStatusId(1);
        request.setPriority(2);
        when(statusRepository.findById(1)).thenReturn(Optional.of(todoStatus));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponse result = taskService.create(request);

        assertThat(result).isNotNull();
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void create_throwsStatusNotFoundException_whenStatusNotExists() {
        TaskRequest request = new TaskRequest();
        request.setTitle("New task");
        request.setStatusId(99);
        when(statusRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(request))
                .isInstanceOf(StatusNotFoundException.class);
    }

    @Test
    void update_updatesTask_whenExists() {
        TaskRequest request = new TaskRequest();
        request.setTitle("Updated title");
        request.setStatusId(1);
        request.setPriority(5);
        when(taskRepository.findById(1)).thenReturn(Optional.of(sampleTask));
        when(statusRepository.findById(1)).thenReturn(Optional.of(todoStatus));
        when(taskRepository.save(any())).thenReturn(sampleTask);

        TaskResponse result = taskService.update(1, request);

        assertThat(result).isNotNull();
        verify(taskRepository).save(sampleTask);
    }

    @Test
    void update_throwsTaskNotFoundException_whenNotExists() {
        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(99, new TaskRequest()))
                .isInstanceOf(TaskNotFoundException.class);
    }

    @Test
    void updateStatus_updatesStatus_whenExists() {
        TaskStatusRequest request = new TaskStatusRequest();
        request.setStatusId(1);
        when(taskRepository.findById(1)).thenReturn(Optional.of(sampleTask));
        when(statusRepository.findById(1)).thenReturn(Optional.of(todoStatus));
        when(taskRepository.save(any())).thenReturn(sampleTask);

        TaskResponse result = taskService.updateStatus(1, request);

        assertThat(result).isNotNull();
    }

    @Test
    void delete_deletesTask_whenExists() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(sampleTask));

        taskService.delete(1);

        verify(taskRepository).delete(sampleTask);
    }

    @Test
    void delete_throwsTaskNotFoundException_whenNotExists() {
        when(taskRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.delete(99))
                .isInstanceOf(TaskNotFoundException.class);
    }
}
