package com.example.task_manager;

import com.example.task_manager.entity.Status;
import com.example.task_manager.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

	private final StatusRepository statusRepository;

	@Override
	public void run(String... args) {
		if (!statusRepository.findByIsDefaultTrue().isEmpty()) {
			return;
		}

		List<Status> defaults = List.of(
				Status.builder().name("To Do").isDefault(true).build(),
				Status.builder().name("Scheduled").isDefault(true).build(),
				Status.builder().name("Doing").isDefault(true).build(),
				Status.builder().name("Done").isDefault(true).build());

		statusRepository.saveAll(defaults);
	}
}