package com.example.baobab_academy.services;

import com.example.baobab_academy.dtos.CategoryResponse;
import com.example.baobab_academy.models.Category;
import com.example.baobab_academy.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Récupère toutes les catégories
     */
    public List<CategoryResponse> getAllCategories() {
        log.info("📁 Récupération de toutes les catégories");
        
        List<Category> categories = categoryRepository.findAll();
        
        return categories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une catégorie par ID
     */
    public CategoryResponse getCategoryById(String categoryId) {
        log.info("📁 Récupération de la catégorie: {}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID: " + categoryId));
        
        return convertToResponse(category);
    }

    /**
     * Vérifie si une catégorie existe
     */
    public boolean categoryExists(String categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    /**
     * Convertit une entité Category en CategoryResponse
     */
    private CategoryResponse convertToResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setCreatedAt(category.getCreatedAt());
        return response;
    }
}