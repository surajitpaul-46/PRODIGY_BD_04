package com.example.rediscache;

import com.example.rediscache.model.User;
import com.example.rediscache.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class RedisCacheApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisCacheApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(UserRepository userRepository) {
		return args -> {
			if (userRepository.count() == 0) {
				userRepository.saveAll(List.of(
						User.builder().name("Alice Smith").email("alice@example.com").role("ADMIN").build(),
						User.builder().name("Bob Jones").email("bob@example.com").role("DEVELOPER").build(),
						User.builder().name("Charlie Brown").email("charlie@example.com").role("USER").build()
				));
				System.out.println("Sample database populated with initial users.");
			}
		};
	}
}
