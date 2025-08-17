package com.example.baobab_academy.services;

import com.example.baobab_academy.models.*;
import com.example.baobab_academy.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserProgressService {

    private final UserProgressRepository userProgressRepository;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    /**
     * Démarre la progression d'un utilisateur pour un cours
     */
    public void startCourseProgress(String userId, String courseId) {
        log.info("🎯 Démarrage de la progression pour l'utilisateur {} sur le cours {}", userId, courseId);

        // Vérifier que le cours existe et est publié
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        if (course.getStatus() != com.example.baobab_academy.models.enums.CourseStatus.PUBLISHED) {
            throw new RuntimeException("Ce cours n'est pas encore publié");
        }

        // Vérifier que l'utilisateur existe
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Vérifier si l'utilisateur n'est pas déjà inscrit
        if (userProgressRepository.existsByUserIdAndCourseId(userId, courseId)) {
            log.warn("⚠️ L'utilisateur {} est déjà inscrit au cours {}", userId, courseId);
            return; // Ne pas créer de doublon
        }

        // Créer des entrées de progression pour TOUTES les leçons
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByOrderIndex(courseId);
        if (chapters.isEmpty()) {
            throw new RuntimeException("Ce cours n'a pas encore de contenu disponible");
        }

        List<UserProgress> progressEntries = new ArrayList<>();

        for (Chapter chapter : chapters) {
            List<Lesson> lessons = lessonRepository.findByChapterIdOrderByOrderIndex(chapter.getId());
            for (Lesson lesson : lessons) {
                UserProgress progress = UserProgress.builder()
                        .userId(userId)
                        .courseId(courseId)
                        .lessonId(lesson.getId())
                        .progressPercentage(0)
                        .watchTimeSeconds(0)
                        .completed(false)
                        .build();
                progressEntries.add(progress);
            }
        }

        if (progressEntries.isEmpty()) {
            throw new RuntimeException("Ce cours n'a pas encore de leçons disponibles");
        }

        userProgressRepository.saveAll(progressEntries);

        // 🆕 NOUVEAUTÉ : Incrémenter le compteur d'étudiants du cours
        course.setStudents(course.getStudents() + 1);
        courseRepository.save(course);

        log.info("✅ Progression initiale créée pour le cours {} - {} leçons initialisées",
                courseId, progressEntries.size());
        log.info("📊 Nombre d'étudiants mis à jour: {}", course.getStudents());
    }

    /**
     * Met à jour la progression d'une leçon
     */
    public UserProgress updateLessonProgress(String userId, String lessonId, int progressPercentage,
            int watchTimeSeconds) {
        log.info("📈 Mise à jour progression leçon {} pour utilisateur {}: {}%", lessonId, userId, progressPercentage);

        // Récupérer la leçon pour obtenir le courseId
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Leçon non trouvée"));

        Chapter chapter = chapterRepository.findById(lesson.getChapterId())
                .orElseThrow(() -> new RuntimeException("Chapitre non trouvé"));

        // Trouver ou créer la progression
        UserProgress progress = userProgressRepository.findByUserIdAndLessonId(userId, lessonId)
                .orElse(UserProgress.builder()
                        .userId(userId)
                        .courseId(chapter.getCourseId())
                        .lessonId(lessonId)
                        .build());

        progress.setProgressPercentage(Math.max(progress.getProgressPercentage(), progressPercentage));
        progress.setWatchTimeSeconds(Math.max(progress.getWatchTimeSeconds(), watchTimeSeconds));

        // Marquer comme complété si 80% ou plus
        if (progressPercentage >= 80 && !progress.isCompleted()) {
            progress.markAsCompleted();
            log.info("🎉 Leçon {} automatiquement marquée comme complétée ({}%)", lessonId, progressPercentage);
        }

        return userProgressRepository.save(progress);
    }

    /**
     * Marque une leçon comme complétée
     */
    public UserProgress markLessonAsCompleted(String userId, String lessonId) {
        log.info("✅ Marquage leçon {} comme complétée pour utilisateur {}", lessonId, userId);

        // Récupérer ou créer la progression
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Leçon non trouvée"));

        Chapter chapter = chapterRepository.findById(lesson.getChapterId())
                .orElseThrow(() -> new RuntimeException("Chapitre non trouvé"));

        UserProgress progress = userProgressRepository.findByUserIdAndLessonId(userId, lessonId)
                .orElse(UserProgress.builder()
                        .userId(userId)
                        .courseId(chapter.getCourseId())
                        .lessonId(lessonId)
                        .progressPercentage(0)
                        .watchTimeSeconds(0)
                        .completed(false)
                        .build());

        // Marquer comme complété à 100%
        progress.setProgressPercentage(100);
        progress.markAsCompleted();

        UserProgress savedProgress = userProgressRepository.save(progress);

        log.info("🎯 Leçon {} marquée comme complétée avec succès", lessonId);
        return savedProgress;
    }

    /**
     * Récupère la progression d'un utilisateur pour un cours
     */
    public CourseProgressSummary getCourseProgress(String userId, String courseId) {
        log.info("📊 Récupération progression cours {} pour utilisateur {}", courseId, userId);

        List<UserProgress> userProgresses = userProgressRepository.findByUserIdAndCourseId(userId, courseId);

        // Compter le total de leçons dans le cours
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByOrderIndex(courseId);
        long totalLessons = chapters.stream()
                .mapToLong(chapter -> lessonRepository.countByChapterId(chapter.getId()))
                .sum();

        long completedLessons = userProgresses.stream()
                .mapToLong(progress -> progress.isCompleted() ? 1 : 0)
                .sum();

        double progressPercentage = totalLessons > 0 ? (double) completedLessons / totalLessons * 100 : 0;

        log.info("📈 Progression calculée: {}/{} leçons ({}%)", completedLessons, totalLessons,
                String.format("%.1f", progressPercentage));

        return CourseProgressSummary.builder()
                .courseId(courseId)
                .userId(userId)
                .totalLessons((int) totalLessons)
                .completedLessons((int) completedLessons)
                .progressPercentage(progressPercentage)
                .isStarted(!userProgresses.isEmpty())
                .isCompleted(totalLessons > 0 && completedLessons == totalLessons)
                .build();
    }

    /**
     * Vérifie si un utilisateur est inscrit à un cours
     */
    public boolean isUserEnrolledInCourse(String userId, String courseId) {
        boolean isEnrolled = userProgressRepository.existsByUserIdAndCourseId(userId, courseId);
        log.info("🔍 Vérification inscription utilisateur {} au cours {}: {}", userId, courseId, isEnrolled);
        return isEnrolled;
    }

    /**
     * 🆕 Récupère la progression détaillée pour toutes les leçons d'un cours
     */
    public List<UserProgress> getDetailedCourseProgress(String userId, String courseId) {
        log.info("📋 Récupération progression détaillée cours {} pour utilisateur {}", courseId, userId);

        List<UserProgress> progresses = userProgressRepository.findByUserIdAndCourseId(userId, courseId);

        // Si aucune progression trouvée mais que l'utilisateur est inscrit,
        // initialiser les progressions manquantes
        if (progresses.isEmpty() && isUserEnrolledInCourse(userId, courseId)) {
            log.info("🔄 Aucune progression détaillée trouvée, initialisation...");
            initializeMissingProgress(userId, courseId);
            progresses = userProgressRepository.findByUserIdAndCourseId(userId, courseId);
        }

        log.info("📊 {} entrées de progression trouvées", progresses.size());
        return progresses;
    }

    /**
     * 🆕 Initialise les progressions manquantes pour un cours
     */
    private void initializeMissingProgress(String userId, String courseId) {
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByOrderIndex(courseId);
        List<UserProgress> newProgresses = new ArrayList<>();

        for (Chapter chapter : chapters) {
            List<Lesson> lessons = lessonRepository.findByChapterIdOrderByOrderIndex(chapter.getId());
            for (Lesson lesson : lessons) {
                // Vérifier si la progression existe déjà
                if (!userProgressRepository.findByUserIdAndLessonId(userId, lesson.getId()).isPresent()) {
                    UserProgress progress = UserProgress.builder()
                            .userId(userId)
                            .courseId(courseId)
                            .lessonId(lesson.getId())
                            .progressPercentage(0)
                            .watchTimeSeconds(0)
                            .completed(false)
                            .build();
                    newProgresses.add(progress);
                }
            }
        }

        if (!newProgresses.isEmpty()) {
            userProgressRepository.saveAll(newProgresses);
            log.info("✅ {} nouvelles progressions initialisées", newProgresses.size());
        }
    }

    /**
     * Classe pour résumer la progression d'un cours
     */
    @lombok.Data
    @lombok.Builder
    public static class CourseProgressSummary {
        private String courseId;
        private String userId;
        private int totalLessons;
        private int completedLessons;
        private double progressPercentage;
        private boolean isStarted;
        private boolean isCompleted;
    }
}