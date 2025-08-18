package com.example.baobab_academy.controllers;

import com.example.baobab_academy.dtos.ApiResponse;
import com.example.baobab_academy.dtos.CourseRatingRequest;
import com.example.baobab_academy.dtos.CourseRatingResponse;
import com.example.baobab_academy.dtos.CourseResponse;
import com.example.baobab_academy.dtos.RatingStatsResponse;
import com.example.baobab_academy.models.User;
import com.example.baobab_academy.models.UserProgress;
import com.example.baobab_academy.services.CoursePublicService;
import com.example.baobab_academy.services.CourseRatingService;
import com.example.baobab_academy.services.UserProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

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
    private final CourseRatingService courseRatingService; 

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
                    UserProgressService.CourseProgressSummary progress = userProgressService.getCourseProgress(userId,
                            courseId);
                    response.setProgressPercentage(progress.getProgressPercentage());
                    response.setCompletedLessons(progress.getCompletedLessons());
                    response.setTotalLessons(progress.getTotalLessons());

                    // 🆕 NOUVEAUTÉ : Récupérer la progression détaillée par leçon
                    List<UserProgress> detailedProgress = userProgressService.getDetailedCourseProgress(userId,
                            courseId);

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

    @Operation(summary = "Noter un cours")
    @PostMapping("/{courseId}/rate")
    public ResponseEntity<ApiResponse<CourseRatingResponse>> rateCourse(
            @PathVariable String courseId,
            @Valid @RequestBody CourseRatingRequest request,
            Authentication authentication) {

        try {
            String userId = getUserIdFromAuthentication(authentication);
            log.info("⭐ Notation du cours {} par l'utilisateur {}", courseId, userId);

            CourseRatingResponse rating = courseRatingService.rateCourse(courseId, userId, request);

            return ResponseEntity.ok(ApiResponse.success("Cours noté avec succès", rating));

        } catch (Exception e) {
            log.error("❌ Erreur lors de la notation du cours {}: {}", courseId, e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la notation: " + e.getMessage()));
        }
    }

    @Operation(summary = "Récupérer ma note pour un cours")
    @GetMapping("/{courseId}/my-rating")
    public ResponseEntity<ApiResponse<CourseRatingResponse>> getMyRating(
            @PathVariable String courseId,
            Authentication authentication) {

        try {
            String userId = getUserIdFromAuthentication(authentication);
            log.info("📊 Récupération note utilisateur {} pour cours {}", userId, courseId);

            CourseRatingResponse rating = courseRatingService.getUserRating(courseId, userId);

            return ResponseEntity.ok(ApiResponse.success("Note récupérée avec succès", rating));

        } catch (Exception e) {
            if (e.getMessage().contains("Aucune note trouvée")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Aucune note trouvée"));
            }

            log.error("❌ Erreur lors de la récupération de la note: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()));
        }
    }

    @Operation(summary = "Récupérer toutes les notes d'un cours")
    @GetMapping("/{courseId}/ratings")
    public ResponseEntity<ApiResponse<Page<CourseRatingResponse>>> getCourseRatings(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<CourseRatingResponse> ratings = courseRatingService.getCourseRatings(courseId, pageable);

            return ResponseEntity.ok(ApiResponse.success("Notes récupérées avec succès", ratings));

        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des notes: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la récupération: " + e.getMessage()));
        }
    }

    @Operation(summary = "Récupérer les statistiques de notation d'un cours")
    @GetMapping("/{courseId}/rating-stats")
    public ResponseEntity<ApiResponse<RatingStatsResponse>> getCourseRatingStats(
            @PathVariable String courseId) {

        try {
            RatingStatsResponse stats = courseRatingService.getCourseRatingStats(courseId);

            return ResponseEntity.ok(ApiResponse.success("Statistiques récupérées avec succès", stats));

        } catch (Exception e) {
            log.error("❌ Erreur lors du calcul des statistiques: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors du calcul: " + e.getMessage()));
        }
    }

    @Operation(summary = "Supprimer ma note pour un cours")
    @DeleteMapping("/{courseId}/my-rating")
    public ResponseEntity<ApiResponse<Void>> deleteMyRating(
            @PathVariable String courseId,
            Authentication authentication) {

        try {
            String userId = getUserIdFromAuthentication(authentication);
            log.info("🗑️ Suppression note utilisateur {} pour cours {}", userId, courseId);

            courseRatingService.deleteRating(courseId, userId);

            return ResponseEntity.ok(ApiResponse.success("Note supprimée avec succès"));

        } catch (Exception e) {
            log.error("❌ Erreur lors de la suppression de la note: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la suppression: " + e.getMessage()));
        }
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