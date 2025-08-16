package com.example.baobab_academy.controllers;

import com.example.baobab_academy.dtos.ApiResponse;
import com.example.baobab_academy.dtos.CategoryResponse;
import com.example.baobab_academy.services.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Categories", description = "Gestion des catégories de cours")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Récupérer toutes les catégories")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        log.info("📁 Récupération de toutes les catégories");
        
        try {
            List<CategoryResponse> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(ApiResponse.success("Catégories récupérées avec succès", categories));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des catégories: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la récupération des catégories: " + e.getMessage()));
        }
    }

    @Operation(summary = "Récupérer une catégorie par ID")
    @GetMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable String categoryId) {
        log.info("📁 Récupération de la catégorie: {}", categoryId);
        
        try {
            CategoryResponse category = categoryService.getCategoryById(categoryId);
            return ResponseEntity.ok(ApiResponse.success("Catégorie récupérée avec succès", category));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération de la catégorie: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Catégorie non trouvée"));
        }
    }
}