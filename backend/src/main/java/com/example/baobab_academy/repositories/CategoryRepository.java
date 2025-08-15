package com.example.baobab_academy.repositories;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.baobab_academy.models.Category;

@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {
    Optional<Category> findByName(String name);
    Boolean existsByName(String name);
}