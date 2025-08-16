package com.example.baobab_academy.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.baobab_academy.dtos.ApiResponse;
import com.example.baobab_academy.dtos.CourseResponse;
import com.example.baobab_academy.services.CoursePublicService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/courses/public")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Public Courses", description = "Accès public aux cours")
public class CoursePublicController {

    private final CoursePublicService coursePublicService;

    @Operation(summary = "Récupérer tous les cours publiés")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> getPublishedCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String search) {

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

            Page<CourseResponse> courses;

            if (search != null && !search.trim().isEmpty()) {
                courses = coursePublicService.searchPublishedCourses(search.trim(), categoryId, pageable);
            } else if (categoryId != null && !categoryId.trim().isEmpty()) {
                courses = coursePublicService.getPublishedCoursesByCategory(categoryId, pageable);
            } else {
                courses = coursePublicService.getAllPublishedCourses(pageable);
            }

            return ResponseEntity.ok(ApiResponse.success("Cours récupérés avec succès", courses));

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des cours: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la récupération des cours: " + e.getMessage()));
        }
    }

    @Operation(summary = "Récupérer un cours publié par ID")
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> getPublishedCourseById(@PathVariable String courseId) {
        try {
            CourseResponse course = coursePublicService.getPublishedCourseById(courseId);
            return ResponseEntity.ok(ApiResponse.success("Cours récupéré avec succès", course));

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération du cours {}: {}", courseId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Cours non trouvé"));
        }
    }

    @Operation(summary = "Récupérer les cours populaires")
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getPopularCourses(
            @RequestParam(defaultValue = "6") int limit) {

        try {
            List<CourseResponse> courses = coursePublicService.getPopularCourses(limit);
            return ResponseEntity.ok(ApiResponse.success("Cours populaires récupérés avec succès", courses));

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des cours populaires: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la récupération des cours populaires"));
        }
    }

    @Operation(summary = "Récupérer les cours les mieux notés")
    @GetMapping("/top-rated")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getTopRatedCourses(
            @RequestParam(defaultValue = "6") int limit) {

        try {
            List<CourseResponse> courses = coursePublicService.getTopRatedCourses(limit);
            return ResponseEntity.ok(ApiResponse.success("Cours les mieux notés récupérés avec succès", courses));

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des cours les mieux notés: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la récupération des cours"));
        }
    }

    @Operation(summary = "Récupérer les derniers cours ajoutés")
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getLatestCourses(
            @RequestParam(defaultValue = "6") int limit) {

        try {
            List<CourseResponse> courses = coursePublicService.getLatestCourses(limit);
            return ResponseEntity.ok(ApiResponse.success("Derniers cours récupérés avec succès", courses));

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des derniers cours: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la récupération des cours"));
        }
    }
}