package com.example.task_manager.repository;

import com.example.task_manager.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Integer> {

    List<Status> findByIsDefaultTrue();
}