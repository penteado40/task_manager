package com.example.task_manager.repository;

import com.example.task_manager.entity.Status;
import com.example.task_manager.entity.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    private Status todoStatus;
    private Status doneStatus;

    @BeforeEach
    void setUp() {
        todoStatus = entityManager.persist(Status.builder().name("To Do").isDefault(true).build());
        doneStatus = entityManager.persist(Status.builder().name("Done").isDefault(true).build());
        entityManager.flush();
    }

    @Test
    void findByStatus_returnsTasksWithGivenStatus() {
        entityManager.persist(Task.builder().title("Task A").status(todoStatus).build());
        entityManager.persist(Task.builder().title("Task B").status(doneStatus).build());
        entityManager.flush();

        List<Task> result = taskRepository.findByStatus(todoStatus);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Task A");
    }

    @Test
    void findByPriority_returnsTasksWithGivenPriority() {
        entityManager.persist(Task.builder().title("High").status(todoStatus).priority(5).build());
        entityManager.persist(Task.builder().title("Low").status(todoStatus).priority(1).build());
        entityManager.flush();

        List<Task> result = taskRepository.findByPriority(5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("High");
    }

    @Test
    void findByPriorityAndStatus_returnsOnlyMatchingTasks() {
        entityManager.persist(Task.builder().title("Match").status(todoStatus).priority(3).build());
        entityManager.persist(Task.builder().title("Wrong status").status(doneStatus).priority(3).build());
        entityManager.persist(Task.builder().title("Wrong priority").status(todoStatus).priority(1).build());
        entityManager.flush();

        List<Task> result = taskRepository.findByPriorityAndStatus(3, todoStatus);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Match");
    }

    @Test
    void findByStatus_returnsEmpty_whenNoTasksWithThatStatus() {
        entityManager.persist(Task.builder().title("Task A").status(todoStatus).build());
        entityManager.flush();

        List<Task> result = taskRepository.findByStatus(doneStatus);

        assertThat(result).isEmpty();
    }
}
