package com.example.baobab_academy.controllers;

import com.example.baobab_academy.dtos.ApiResponse;
import com.example.baobab_academy.dtos.CourseResponse;
import com.example.baobab_academy.models.User;
import com.example.baobab_academy.services.CoursePublicService;
import com.example.baobab_academy.repositories.UserProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Profile", description = "Endpoints pour le profil utilisateur")
public class UserProfileController {

    private final UserProgressRepository userProgressRepository;
    private final CoursePublicService coursePublicService;

    @Operation(summary = "Récupérer tous les cours inscrits avec progression")
    @GetMapping("/courses/enrolled")
    public ResponseEntity<ApiResponse<List<EnrolledCourseDto>>> getEnrolledCourses(
            Authentication authentication) {
        
        try {
            String userId = getUserIdFromAuthentication(authentication);
            log.info("📚 Récupération des cours inscrits pour l'utilisateur: {}", userId);
            
            // Récupérer toutes les progressions de l'utilisateur
            List<com.example.baobab_academy.models.UserProgress> userProgresses = 
                    userProgressRepository.findByUserId(userId);
            
            // Grouper par cours
            Map<String, List<com.example.baobab_academy.models.UserProgress>> progressByCourse = 
                    userProgresses.stream()
                            .collect(Collectors.groupingBy(com.example.baobab_academy.models.UserProgress::getCourseId));
            
            List<EnrolledCourseDto> enrolledCourses = progressByCourse.entrySet().stream()
                    .map(entry -> {
                        String courseId = entry.getKey();
                        List<com.example.baobab_academy.models.UserProgress> courseProgresses = entry.getValue();
                        
                        try {
                            // Récupérer le cours complet via le service public
                            CourseResponse course = coursePublicService.getPublishedCourseById(courseId);
                            
                            // Calculer la progression
                            long totalLessons = courseProgresses.size();
                            long completedLessons = courseProgresses.stream()
                                    .mapToLong(progress -> progress.isCompleted() ? 1 : 0)
                                    .sum();
                            
                            double progressPercentage = totalLessons > 0 ? 
                                    (double) completedLessons / totalLessons * 100 : 0;
                            
                            boolean isCompleted = totalLessons > 0 && completedLessons == totalLessons;
                            
                            // Mapper vers le DTO
                            return EnrolledCourseDto.builder()
                                    .id(course.getId())
                                    .title(course.getTitle())
                                    .description(course.getDescription())
                                    .coverImage(course.getCoverImage()) // ✅ Image de couverture
                                    .categoryId(course.getCategoryId())
                                    .categoryName(course.getCategoryName())
                                    .instructorId(course.getInstructorId())
                                    .level(course.getLevel().toString())
                                    .duration(course.getDuration())
                                    .students(course.getStudents())
                                    .rating(course.getRating())
                                    .status(course.getStatus().toString())
                                    .createdAt(course.getCreatedAt().toString())
                                    .updatedAt(course.getUpdatedAt() != null ? course.getUpdatedAt().toString() : null)
                                    .progressPercentage(progressPercentage)
                                    .completedLessons((int) completedLessons)
                                    .totalLessons((int) totalLessons)
                                    .enrolledAt(courseProgresses.get(0).getCreatedAt().toString())
                                    .lastAccessedAt(courseProgresses.stream()
                                            .map(p -> p.getUpdatedAt() != null ? p.getUpdatedAt() : p.getCreatedAt())
                                            .max(java.time.LocalDateTime::compareTo)
                                            .map(Object::toString)
                                            .orElse(null))
                                    .isCompleted(isCompleted)
                                    .build();
                                    
                        } catch (Exception e) {
                            log.error("❌ Erreur lors de la récupération du cours {}: {}", courseId, e.getMessage());
                            return null;
                        }
                    })
                    .filter(course -> course != null)
                    .collect(Collectors.toList());
            
            log.info("✅ {} cours inscrits récupérés pour l'utilisateur {}", enrolledCourses.size(), userId);
            return ResponseEntity.ok(ApiResponse.success("Cours inscrits récupérés avec succès", enrolledCourses));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des cours inscrits: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la récupération des cours: " + e.getMessage()));
        }
    }

    @Operation(summary = "Récupérer les cours en cours")
    @GetMapping("/courses/in-progress") 
    public ResponseEntity<ApiResponse<List<EnrolledCourseDto>>> getInProgressCourses(
            Authentication authentication) {
        
        try {
            String userId = getUserIdFromAuthentication(authentication);
            log.info("🔄 Récupération des cours en cours pour l'utilisateur: {}", userId);
            
            ApiResponse<List<EnrolledCourseDto>> enrolledResponse = getEnrolledCourses(authentication).getBody();
            
            if (enrolledResponse != null && enrolledResponse.isSuccess() && enrolledResponse.getData() != null) {
                List<EnrolledCourseDto> inProgressCourses = enrolledResponse.getData().stream()
                        .filter(course -> course.getProgressPercentage() > 0 && course.getProgressPercentage() < 100)
                        .collect(Collectors.toList());
                
                return ResponseEntity.ok(ApiResponse.success("Cours en cours récupérés avec succès", inProgressCourses));
            }
            
            return ResponseEntity.ok(ApiResponse.success("Aucun cours en cours", List.of()));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des cours en cours: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la récupération des cours: " + e.getMessage()));
        }
    }

    @Operation(summary = "Récupérer les cours terminés")
    @GetMapping("/courses/completed")
    public ResponseEntity<ApiResponse<List<EnrolledCourseDto>>> getCompletedCourses(
            Authentication authentication) {
        
        try {
            String userId = getUserIdFromAuthentication(authentication);
            log.info("🏆 Récupération des cours terminés pour l'utilisateur: {}", userId);
            
            ApiResponse<List<EnrolledCourseDto>> enrolledResponse = getEnrolledCourses(authentication).getBody();
            
            if (enrolledResponse != null && enrolledResponse.isSuccess() && enrolledResponse.getData() != null) {
                List<EnrolledCourseDto> completedCourses = enrolledResponse.getData().stream()
                        .filter(course -> course.getIsCompleted() || course.getProgressPercentage() >= 100)
                        .collect(Collectors.toList());
                
                return ResponseEntity.ok(ApiResponse.success("Cours terminés récupérés avec succès", completedCourses));
            }
            
            return ResponseEntity.ok(ApiResponse.success("Aucun cours terminé", List.of()));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des cours terminés: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la récupération des cours: " + e.getMessage()));
        }
    }

    @Operation(summary = "Récupérer les statistiques utilisateur")
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<UserStatsDto>> getUserStats(
            Authentication authentication) {
        
        try {
            String userId = getUserIdFromAuthentication(authentication);
            log.info("📊 Récupération des statistiques pour l'utilisateur: {}", userId);
            
            ApiResponse<List<EnrolledCourseDto>> enrolledResponse = getEnrolledCourses(authentication).getBody();
            
            if (enrolledResponse != null && enrolledResponse.isSuccess() && enrolledResponse.getData() != null) {
                List<EnrolledCourseDto> enrolledCourses = enrolledResponse.getData();
                
                int completedCourses = (int) enrolledCourses.stream()
                        .filter(course -> course.getIsCompleted() || course.getProgressPercentage() >= 100)
                        .count();
                
                int inProgressCourses = (int) enrolledCourses.stream()
                        .filter(course -> course.getProgressPercentage() > 0 && course.getProgressPercentage() < 100)
                        .count();
                
                double completionRate = enrolledCourses.isEmpty() ? 0.0 : 
                        (double) completedCourses / enrolledCourses.size() * 100;
                
                // Estimation du temps d'étude
                int totalWatchTimeHours = enrolledCourses.stream()
                        .mapToInt(course -> {
                            String duration = course.getDuration();
                            int estimatedHours = extractHoursFromDuration(duration);
                            return (int) (estimatedHours * (course.getProgressPercentage() / 100.0));
                        })
                        .sum();
                
                UserStatsDto stats = UserStatsDto.builder()
                        .totalEnrolledCourses(enrolledCourses.size())
                        .completedCourses(completedCourses)
                        .inProgressCourses(inProgressCourses)
                        .totalWatchTimeHours(totalWatchTimeHours)
                        .completionRate(completionRate)
                        .certificatesEarned(completedCourses)
                        .build();
                
                return ResponseEntity.ok(ApiResponse.success("Statistiques récupérées avec succès", stats));
            }
            
            // Statistiques par défaut si aucun cours
            UserStatsDto emptyStats = UserStatsDto.builder()
                    .totalEnrolledCourses(0)
                    .completedCourses(0)
                    .inProgressCourses(0)
                    .totalWatchTimeHours(0)
                    .completionRate(0.0)
                    .certificatesEarned(0)
                    .build();
            
            return ResponseEntity.ok(ApiResponse.success("Statistiques récupérées avec succès", emptyStats));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des statistiques: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la récupération des statistiques: " + e.getMessage()));
        }
    }

    @Operation(summary = "Récupérer l'activité récente")
    @GetMapping("/activity/recent")
    public ResponseEntity<ApiResponse<List<Object>>> getRecentActivity(
            Authentication authentication) {
        
        try {
            String userId = getUserIdFromAuthentication(authentication);
            log.info("🔔 Récupération de l'activité récente pour l'utilisateur: {}", userId);
            
            // Pour l'instant, retourner une liste vide
            // Vous pouvez implémenter la logique d'activité plus tard
            List<Object> activities = List.of();
            
            return ResponseEntity.ok(ApiResponse.success("Activité récente récupérée avec succès", activities));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération de l'activité: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la récupération de l'activité: " + e.getMessage()));
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
     * Extrait le nombre d'heures d'une chaîne de durée
     */
    private int extractHoursFromDuration(String duration) {
        if (duration == null) return 2;
        
        try {
            if (duration.toLowerCase().contains("heure")) {
                return Integer.parseInt(duration.replaceAll("[^0-9]", ""));
            } else if (duration.toLowerCase().contains("h")) {
                String[] parts = duration.split("h");
                return Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
            } else if (duration.toLowerCase().contains("minute")) {
                int minutes = Integer.parseInt(duration.replaceAll("[^0-9]", ""));
                return Math.max(1, minutes / 60);
            }
            return 2; // Valeur par défaut
        } catch (Exception e) {
            return 2; // Valeur par défaut en cas d'erreur
        }
    }

    // DTOs
    @lombok.Data
    @lombok.Builder
    public static class EnrolledCourseDto {
        private String id;
        private String title;
        private String description;
        private String coverImage; // ✅ Champ important pour l'image
        private String categoryId;
        private String categoryName;
        private String instructorId;
        private String level;
        private String duration;
        private Integer students;
        private Double rating;
        private String status;
        private String createdAt;
        private String updatedAt;
        private Double progressPercentage;
        private Integer completedLessons;
        private Integer totalLessons;
        private String enrolledAt;
        private String lastAccessedAt;
        private Boolean isCompleted;
    }

    @lombok.Data
    @lombok.Builder
    public static class UserStatsDto {
        private Integer totalEnrolledCourses;
        private Integer completedCourses;
        private Integer inProgressCourses;
        private Integer totalWatchTimeHours;
        private Double completionRate;
        private Integer certificatesEarned;
    }
}