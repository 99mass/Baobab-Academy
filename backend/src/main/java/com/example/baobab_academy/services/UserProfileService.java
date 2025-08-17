package com.example.baobab_academy.services;

import com.example.baobab_academy.models.*;
import com.example.baobab_academy.repositories.*;
import com.example.baobab_academy.dtos.CourseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserProfileService {

    private final UserProgressRepository userProgressRepository;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final CoursePublicService coursePublicService;

    /**
     * Récupère tous les cours auxquels l'utilisateur est inscrit avec leur progression
     */
    public List<EnrolledCourseDto> getEnrolledCourses(String userId) {
        log.info("📚 Récupération des cours inscrits pour l'utilisateur: {}", userId);
        
        try {
            // Récupérer toutes les progressions de l'utilisateur
            List<UserProgress> userProgresses = userProgressRepository.findByUserId(userId);
            log.info("🔍 Trouvé {} progressions pour l'utilisateur {}", userProgresses.size(), userId);
            
            if (userProgresses.isEmpty()) {
                log.info("ℹ️ Aucune progression trouvée pour l'utilisateur {}", userId);
                return List.of();
            }
            
            // Grouper par cours
            Map<String, List<UserProgress>> progressByCourse = userProgresses.stream()
                    .collect(Collectors.groupingBy(UserProgress::getCourseId));
            
            log.info("📊 Progression groupée pour {} cours", progressByCourse.size());
            
            return progressByCourse.entrySet().stream()
                    .map(entry -> {
                        String courseId = entry.getKey();
                        List<UserProgress> courseProgresses = entry.getValue();
                        
                        try {
                            return mapToEnrolledCourseDto(courseId, courseProgresses);
                        } catch (Exception e) {
                            log.error("❌ Erreur lors du mapping du cours {}: {}", courseId, e.getMessage());
                            return null;
                        }
                    })
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des cours inscrits: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la récupération des cours inscrits", e);
        }
    }

    /**
     * Récupère les cours en cours (progression > 0 et < 100)
     */
    public List<EnrolledCourseDto> getInProgressCourses(String userId) {
        log.info("🔄 Récupération des cours en cours pour l'utilisateur: {}", userId);
        
        return getEnrolledCourses(userId).stream()
                .filter(course -> course.getProgressPercentage() > 0 && course.getProgressPercentage() < 100)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les cours terminés (progression = 100)
     */
    public List<EnrolledCourseDto> getCompletedCourses(String userId) {
        log.info("🏆 Récupération des cours terminés pour l'utilisateur: {}", userId);
        
        return getEnrolledCourses(userId).stream()
                .filter(course -> course.getIsCompleted() || course.getProgressPercentage() >= 100)
                .collect(Collectors.toList());
    }

    /**
     * Calcule les statistiques de l'utilisateur
     */
    public UserStatsDto getUserStats(String userId) {
        log.info("📊 Calcul des statistiques pour l'utilisateur: {}", userId);
        
        List<EnrolledCourseDto> enrolledCourses = getEnrolledCourses(userId);
        List<EnrolledCourseDto> completedCourses = getCompletedCourses(userId);
        List<EnrolledCourseDto> inProgressCourses = getInProgressCourses(userId);
        
        // Calculer le temps d'étude total (estimation)
        int totalWatchTimeHours = enrolledCourses.stream()
                .mapToInt(course -> {
                    String duration = course.getDuration();
                    int estimatedHours = extractHoursFromDuration(duration);
                    return (int) (estimatedHours * (course.getProgressPercentage() / 100.0));
                })
                .sum();
        
        double completionRate = enrolledCourses.isEmpty() ? 0.0 : 
                (double) completedCourses.size() / enrolledCourses.size() * 100;
        
        UserStatsDto stats = UserStatsDto.builder()
                .totalEnrolledCourses(enrolledCourses.size())
                .completedCourses(completedCourses.size())
                .inProgressCourses(inProgressCourses.size())
                .totalWatchTimeHours(totalWatchTimeHours)
                .completionRate(completionRate)
                .certificatesEarned(completedCourses.size()) // 1 certificat par cours terminé
                .build();
        
        log.info("✅ Statistiques calculées: {}", stats);
        return stats;
    }

    /**
     * Récupère l'activité récente de l'utilisateur
     */
    public List<UserActivityDto> getRecentActivity(String userId) {
        log.info("🔔 Récupération de l'activité récente pour l'utilisateur: {}", userId);
        
        try {
            // Récupérer les progressions récentes (les 10 dernières)
            List<UserProgress> recentProgresses = userProgressRepository
                    .findTop10ByUserIdOrderByUpdatedAtDesc(userId);
            
            return recentProgresses.stream()
                    .map(this::mapToUserActivityDto)
                    .filter(activity -> activity != null)
                    .collect(Collectors.toList());
                    
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération de l'activité récente: {}", e.getMessage());
            return List.of(); // Retourner une liste vide en cas d'erreur
        }
    }

    /**
     * Mappe un courseId et ses progressions vers EnrolledCourseDto
     */
    private EnrolledCourseDto mapToEnrolledCourseDto(String courseId, List<UserProgress> progressList) {
        try {
            // Récupérer le cours via le service public (comme dans AdminDashboard)
            CourseResponse course = coursePublicService.getPublishedCourseById(courseId);
            
            // Calculer la progression globale
            long totalLessons = getTotalLessonsForCourse(courseId);
            long completedLessons = progressList.stream()
                    .mapToLong(progress -> progress.isCompleted() ? 1 : 0)
                    .sum();
            
            double progressPercentage = totalLessons > 0 ? 
                    (double) completedLessons / totalLessons * 100 : 0;
            
            boolean isCompleted = totalLessons > 0 && completedLessons == totalLessons;
            
            // Trouver les dates d'inscription et de dernière activité
            LocalDateTime enrolledAt = progressList.stream()
                    .map(UserProgress::getCreatedAt)
                    .min(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());
            
            LocalDateTime lastAccessedAt = progressList.stream()
                    .map(progress -> progress.getUpdatedAt() != null ? progress.getUpdatedAt() : progress.getCreatedAt())
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
            
            // 🔍 Log pour debug des images
            log.debug("🖼️ Cours {}: Image de couverture = {}", course.getTitle(), course.getCoverImage());
            
            return EnrolledCourseDto.builder()
                    .id(course.getId())
                    .title(course.getTitle())
                    .description(course.getDescription())
                    .coverImage(course.getCoverImage()) // ✅ Image de couverture du CourseResponse
                    .categoryId(course.getCategoryId())
                    .categoryName(course.getCategoryName())
                    .instructorId(course.getInstructorId())
                    .level(course.getLevel().toString())
                    .duration(course.getDuration())
                    .students(course.getStudents())
                    .rating(course.getRating())
                    .status(course.getStatus().toString())
                    .createdAt(course.getCreatedAt() != null ? 
                        course.getCreatedAt().toString() : LocalDateTime.now().toString())
                    .updatedAt(course.getUpdatedAt() != null ? 
                        course.getUpdatedAt().toString() : null)
                    .progressPercentage(progressPercentage)
                    .completedLessons((int) completedLessons)
                    .totalLessons((int) totalLessons)
                    .enrolledAt(enrolledAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .lastAccessedAt(lastAccessedAt != null ? 
                        lastAccessedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                    .isCompleted(isCompleted)
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ Erreur lors du mapping du cours {}: {}", courseId, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Mappe une progression vers une activité utilisateur
     */
    private UserActivityDto mapToUserActivityDto(UserProgress progress) {
        try {
            Lesson lesson = lessonRepository.findById(progress.getLessonId()).orElse(null);
            if (lesson == null) {
                log.warn("⚠️ Leçon non trouvée: {}", progress.getLessonId());
                return null;
            }
            
            Chapter chapter = chapterRepository.findById(lesson.getChapterId()).orElse(null);
            if (chapter == null) {
                log.warn("⚠️ Chapitre non trouvé: {}", lesson.getChapterId());
                return null;
            }
            
            Course course = courseRepository.findById(chapter.getCourseId()).orElse(null);
            if (course == null) {
                log.warn("⚠️ Cours non trouvé: {}", chapter.getCourseId());
                return null;
            }
            
            String type = progress.isCompleted() ? "LESSON_COMPLETED" : "LESSON_PROGRESS";
            String description = progress.isCompleted() ? 
                "Leçon terminée: " + lesson.getTitle() :
                "Progression mise à jour: " + lesson.getTitle();
            
            return UserActivityDto.builder()
                    .id(progress.getId())
                    .type(type)
                    .description(description)
                    .courseId(course.getId())
                    .courseTitle(course.getTitle())
                    .lessonId(lesson.getId())
                    .lessonTitle(lesson.getTitle())
                    .createdAt(progress.getUpdatedAt() != null ? 
                        progress.getUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) :
                        progress.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ Erreur lors du mapping de l'activité: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Compte le nombre total de leçons dans un cours
     */
    private long getTotalLessonsForCourse(String courseId) {
        try {
            List<Chapter> chapters = chapterRepository.findByCourseIdOrderByOrderIndex(courseId);
            return chapters.stream()
                    .mapToLong(chapter -> lessonRepository.countByChapterId(chapter.getId()))
                    .sum();
        } catch (Exception e) {
            log.error("❌ Erreur lors du comptage des leçons pour le cours {}: {}", courseId, e.getMessage());
            return 0;
        }
    }

    /**
     * Extrait le nombre d'heures d'une chaîne de durée
     */
    private int extractHoursFromDuration(String duration) {
        if (duration == null || duration.trim().isEmpty()) {
            return 2; // Valeur par défaut
        }
        
        try {
            String cleanDuration = duration.toLowerCase().trim();
            
            // Formats possibles: "5 heures", "2h30", "90 minutes", etc.
            if (cleanDuration.contains("heure")) {
                String numberStr = cleanDuration.replaceAll("[^0-9]", "");
                return numberStr.isEmpty() ? 2 : Integer.parseInt(numberStr);
            } else if (cleanDuration.contains("h")) {
                String[] parts = cleanDuration.split("h");
                if (parts.length > 0) {
                    String numberStr = parts[0].replaceAll("[^0-9]", "");
                    return numberStr.isEmpty() ? 2 : Integer.parseInt(numberStr);
                }
            } else if (cleanDuration.contains("minute")) {
                String numberStr = cleanDuration.replaceAll("[^0-9]", "");
                if (!numberStr.isEmpty()) {
                    int minutes = Integer.parseInt(numberStr);
                    return Math.max(1, minutes / 60);
                }
            } else {
                // Essayer d'extraire juste le premier nombre
                String numberStr = cleanDuration.replaceAll("[^0-9]", "");
                if (!numberStr.isEmpty()) {
                    return Integer.parseInt(numberStr);
                }
            }
            
            return 2; // Valeur par défaut
        } catch (Exception e) {
            log.warn("⚠️ Impossible de parser la durée: '{}', utilisation de la valeur par défaut", duration);
            return 2; // Valeur par défaut
        }
    }

    // DTOs intégrés (peuvent aussi être dans des fichiers séparés)
    @lombok.Data
    @lombok.Builder
    public static class EnrolledCourseDto {
        private String id;
        private String title;
        private String description;
        private String coverImage; // ✅ Image de couverture
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

    @lombok.Data
    @lombok.Builder
    public static class UserActivityDto {
        private String id;
        private String type; // "COURSE_ENROLLED", "LESSON_COMPLETED", "COURSE_COMPLETED"
        private String description;
        private String courseId;
        private String courseTitle;
        private String lessonId;
        private String lessonTitle;
        private String createdAt;
    }
}