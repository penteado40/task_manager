package com.example.task_manager.repository;

import com.example.task_manager.entity.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StatusRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StatusRepository statusRepository;

    @Test
    void findByIsDefaultTrue_returnsOnlyDefaultStatuses() {
        entityManager.persist(Status.builder().name("To Do").isDefault(true).build());
        entityManager.persist(Status.builder().name("Custom").isDefault(false).build());
        entityManager.flush();

        List<Status> result = statusRepository.findByIsDefaultTrue();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("To Do");
        assertThat(result.get(0).isDefault()).isTrue();
    }

    @Test
    void findByIsDefaultTrue_returnsEmpty_whenNoDefaultsExist() {
        entityManager.persist(Status.builder().name("Custom").isDefault(false).build());
        entityManager.flush();

        List<Status> result = statusRepository.findByIsDefaultTrue();

        assertThat(result).isEmpty();
    }

    @Test
    void findByIsDefaultTrue_returnsMultiple_whenSeveralDefaultsExist() {
        entityManager.persist(Status.builder().name("To Do").isDefault(true).build());
        entityManager.persist(Status.builder().name("Doing").isDefault(true).build());
        entityManager.persist(Status.builder().name("Done").isDefault(true).build());
        entityManager.flush();

        List<Status> result = statusRepository.findByIsDefaultTrue();

        assertThat(result).hasSize(3);
    }
}
