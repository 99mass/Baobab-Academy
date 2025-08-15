package com.example.baobab_academy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableMongoRepositories(basePackages = "com.example.baobab_academy.repositories")
@SpringBootApplication
public class BaobabAcademyApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaobabAcademyApplication.class, args);
	}

}
