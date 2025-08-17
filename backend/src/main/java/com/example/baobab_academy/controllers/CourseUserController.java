package com.example.baobab_academy.controllers;

import com.example.baobab_academy.dtos.ApiResponse;
import com.example.baobab_academy.dtos.CourseResponse;
import com.example.baobab_academy.models.User;
import com.example.baobab_academy.models.UserProgress;
import com.example.baobab_academy.services.CoursePublicService;
import com.example.baobab_academy.services.UserProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Courses", description = "Accès aux cours pour les utilisateurs authentifiés")
public class CourseUserController {

    private final CoursePublicService coursePublicService;
    private final UserProgressService userProgressService;

    @Operation(summary = "Récupérer un cours avec progression utilisateur")
    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseWithProgressResponse>> getCourseWithProgress(
            @PathVariable String courseId,
            Authentication authentication) {
        
        try {
            // Récupérer le cours public
            CourseResponse course = coursePublicService.getPublishedCourseById(courseId);
            
            CourseWithProgressResponse response = new CourseWithProgressResponse();
            response.setCourse(course);
            response.setEnrolled(false);
            response.setProgressPercentage(0.0);
            
            // Si l'utilisateur est authentifié, ajouter la progression
            if (authentication != null && authentication.isAuthenticated()) {
                String userId = getUserIdFromAuthentication(authentication);
                
                // Vérifier si l'utilisateur est inscrit
                boolean isEnrolled = userProgressService.isUserEnrolledInCourse(userId, courseId);
                response.setEnrolled(isEnrolled);
                
                if (isEnrolled) {
                    // Récupérer la progression globale
                    UserProgressService.CourseProgressSummary progress = 
                            userProgressService.getCourseProgress(userId, courseId);
                    response.setProgressPercentage(progress.getProgressPercentage());
                    response.setCompletedLessons(progress.getCompletedLessons());
                    response.setTotalLessons(progress.getTotalLessons());
                    
                    // 🆕 NOUVEAUTÉ : Récupérer la progression détaillée par leçon
                    List<UserProgress> detailedProgress = 
                            userProgressService.getDetailedCourseProgress(userId, courseId);
                    
                    List<UserLessonProgressDto> userProgress = detailedProgress.stream()
                            .map(this::mapToUserLessonProgressDto)
                            .collect(Collectors.toList());
                    
                    response.setUserProgress(userProgress);
                    
                    log.info("📊 Progression détaillée récupérée: {} leçons avec progression", userProgress.size());
                }
            }
            
            return ResponseEntity.ok(ApiResponse.success("Cours récupéré avec succès", response));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération du cours {}: {}", courseId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Cours non trouvé"));
        }
    }

    @Operation(summary = "S'inscrire à un cours")
    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<ApiResponse<Object>> enrollInCourse(
            @PathVariable String courseId,
            Authentication authentication) {
        
        try {
            String userId = getUserIdFromAuthentication(authentication);
            log.info("🎯 Tentative d'inscription utilisateur {} au cours {}", userId, courseId);
            
            // Vérifier que le cours existe et est publié
            CourseResponse course = coursePublicService.getPublishedCourseById(courseId);
            if (course == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Cours non trouvé ou non publié"));
            }
            
            // Vérifier si déjà inscrit
            if (userProgressService.isUserEnrolledInCourse(userId, courseId)) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Vous êtes déjà inscrit à ce cours"));
            }
            
            // Démarrer la progression
            userProgressService.startCourseProgress(userId, courseId);
            
            log.info("✅ Utilisateur {} inscrit au cours {}", userId, courseId);
            return ResponseEntity.ok(ApiResponse.success("Inscription réussie au cours"));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'inscription au cours {}: {}", courseId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de l'inscription: " + e.getMessage()));
        }
    }

    @Operation(summary = "Marquer une leçon comme complétée")
    @PostMapping("/lessons/{lessonId}/complete")
    public ResponseEntity<ApiResponse<Object>> markLessonAsCompleted(
            @PathVariable String lessonId,
            Authentication authentication) {
        
        try {
            String userId = getUserIdFromAuthentication(authentication);
            log.info("✅ Marquage leçon {} comme complétée pour utilisateur {}", lessonId, userId);
            
            userProgressService.markLessonAsCompleted(userId, lessonId);
            
            return ResponseEntity.ok(ApiResponse.success("Leçon marquée comme complétée"));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors du marquage de la leçon {}: {}", lessonId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors du marquage: " + e.getMessage()));
        }
    }

    @Operation(summary = "Mettre à jour la progression d'une leçon")
    @PutMapping("/lessons/{lessonId}/progress")
    public ResponseEntity<ApiResponse<Object>> updateLessonProgress(
            @PathVariable String lessonId,
            @RequestParam int progressPercentage,
            @RequestParam(defaultValue = "0") int watchTimeSeconds,
            Authentication authentication) {
        
        try {
            String userId = getUserIdFromAuthentication(authentication);
            
            userProgressService.updateLessonProgress(userId, lessonId, progressPercentage, watchTimeSeconds);
            
            return ResponseEntity.ok(ApiResponse.success("Progression mise à jour"));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la mise à jour de progression {}: {}", lessonId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la mise à jour: " + e.getMessage()));
        }
    }

    /**
     * Extrait l'ID utilisateur depuis l'authentification
     */
    private String getUserIdFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return user.getId();
        }
        return authentication.getName();
    }

    /**
     * 🆕 Mapper UserProgress vers DTO pour le frontend
     */
    private UserLessonProgressDto mapToUserLessonProgressDto(UserProgress userProgress) {
        UserLessonProgressDto dto = new UserLessonProgressDto();
        dto.setLessonId(userProgress.getLessonId());
        dto.setCompleted(userProgress.isCompleted());
        dto.setProgressPercentage(userProgress.getProgressPercentage());
        dto.setWatchTimeSeconds(userProgress.getWatchTimeSeconds());
        return dto;
    }

    // 🆕 DTO pour la réponse avec progression
    @lombok.Data
    public static class CourseWithProgressResponse {
        private CourseResponse course;
        private boolean isEnrolled;
        private double progressPercentage;
        private int completedLessons;
        private int totalLessons;
        private List<UserLessonProgressDto> userProgress; // 🆕 AJOUTÉ
    }

    // 🆕 DTO pour la progression par leçon
    @lombok.Data
    public static class UserLessonProgressDto {
        private String lessonId;
        private boolean completed;
        private int progressPercentage;
        private int watchTimeSeconds;
    }
}