package com.example.baobab_academy.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.baobab_academy.models.Category;
import com.example.baobab_academy.models.User;
import com.example.baobab_academy.models.enums.UserRole;
import com.example.baobab_academy.repositories.CategoryRepository;
import com.example.baobab_academy.repositories.UserRepository;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Initialisation des données...");

        initializeCategories();
        initializeUsers();

        log.info("Initialisation des données terminée.");
    }

    private void initializeCategories() {
        if (categoryRepository.count() == 0) {
            List<Category> categories = Arrays.asList(
                    new Category(null,"Informatique", null),
                    new Category(null,"Design", null),
                    new Category(null,"Histoire", null),
                    new Category(null,"Marketing", null),
                    new Category(null,"Management", null),
                    new Category(null,"Sociologie", null),
                    new Category(null,"Sciences", null),
                    new Category(null,"Philosophie", null),
                    new Category(null,"Mathematiques", null),
                    new Category(null,"Physique", null),
                    new Category(null,"Chimie", null),
                    new Category(null,"Biologie", null),
                    new Category(null,"Astronomie", null),
                    new Category(null,"Geographie", null)

            );

            categoryRepository.saveAll(categories);
            log.info("Catégories initialisées: {}", categories.size());
        }
    }

    private void initializeUsers() {
        // Créer un admin par défaut
        if (!userRepository.existsByEmail("admin@baobabacademy.com")) {
            User admin = new User(
                    "admin@baobabacademy.com",
                    passwordEncoder.encode("admin123!"),
                    "Admin",
                    "Baobab Academy",
                    UserRole.ADMIN
            );

            userRepository.save(admin);
            log.info("Utilisateur admin créé: {}", admin.getEmail());
        }

        // Créer un utilisateur test
        if (!userRepository.existsByEmail("user@baobabacademy.com")) {
            User user = new User(
                    "user@baobabacademy.com",
                    passwordEncoder.encode("user123!"),
                    "Utilisateur",
                    "Test",
                    UserRole.USER);

            userRepository.save(user);
            log.info("Utilisateur test créé: {}", user.getEmail());
        }
    }
}