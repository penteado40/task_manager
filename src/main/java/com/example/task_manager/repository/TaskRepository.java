package com.example.task_manager.repository;

import com.example.task_manager.entity.Status;
import com.example.task_manager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
// import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByStatus(Status status);

    List<Task> findByPriority(Integer priority);

    List<Task> findByPriorityAndStatus(Integer priority, Status status);
}