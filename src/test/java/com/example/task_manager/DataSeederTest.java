package com.example.task_manager;

import com.example.task_manager.entity.Status;
import com.example.task_manager.repository.StatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataSeederTest {

    @Mock
    private StatusRepository statusRepository;

    @InjectMocks
    private DataSeeder dataSeeder;

    @Test
    void run_insertsDefaultStatuses_whenNoneExist() throws Exception {
        when(statusRepository.findByIsDefaultTrue()).thenReturn(List.of());

        dataSeeder.run();

        verify(statusRepository).saveAll(argThat(list ->
                ((List<Status>) list).size() == 4 &&
                ((List<Status>) list).stream().allMatch(Status::isDefault)
        ));
    }

    @Test
    void run_doesNotInsert_whenDefaultsAlreadyExist() throws Exception {
        Status existing = Status.builder().id(1).name("To Do").isDefault(true).build();
        when(statusRepository.findByIsDefaultTrue()).thenReturn(List.of(existing));

        dataSeeder.run();

        verify(statusRepository, never()).saveAll(any());
    }
}
