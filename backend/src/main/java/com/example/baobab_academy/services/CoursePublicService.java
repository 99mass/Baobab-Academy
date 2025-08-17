package com.example.baobab_academy.services;

import com.example.baobab_academy.models.Course;
import com.example.baobab_academy.models.Chapter;
import com.example.baobab_academy.models.Lesson;
import com.example.baobab_academy.dtos.ChapterResponse;
import com.example.baobab_academy.dtos.CourseResponse;
import com.example.baobab_academy.dtos.LessonResponse;
import com.example.baobab_academy.models.enums.CourseStatus;
import com.example.baobab_academy.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CoursePublicService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final UserProgressRepository userProgressRepository;
    private final ModelMapper modelMapper;

    /**
     * Récupère tous les cours publiés avec pagination
     */
    public Page<CourseResponse> getAllPublishedCourses(Pageable pageable) {
        log.info("📚 Récupération des cours publiés - Page: {}, Taille: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Course> courses = courseRepository.findByStatus(CourseStatus.PUBLISHED, pageable);
        return courses.map(this::mapToCourseResponse);
    }

    /**
     * Récupère les cours publiés par catégorie
     */
    public Page<CourseResponse> getPublishedCoursesByCategory(String categoryId, Pageable pageable) {
        log.info("📚 Récupération des cours de la catégorie: {}", categoryId);

        Page<Course> courses = courseRepository.findByCategoryIdAndStatus(categoryId, CourseStatus.PUBLISHED, pageable);
        return courses.map(this::mapToCourseResponse);
    }

    /**
     * Recherche de cours publiés
     */
    public Page<CourseResponse> searchPublishedCourses(String searchTerm, String categoryId, Pageable pageable) {
        log.info("🔍 Recherche de cours: '{}' dans la catégorie: {}", searchTerm, categoryId);

        Page<Course> courses;

        if (categoryId != null && !categoryId.trim().isEmpty()) {
            // Recherche dans une catégorie spécifique
            courses = courseRepository.searchByTitleOrDescriptionAndStatus(searchTerm, CourseStatus.PUBLISHED, pageable)
                    .map(course -> course.getCategoryId().equals(categoryId) ? course : null)
                    .map(Course.class::cast);
        } else {
            // Recherche globale
            courses = courseRepository.searchByTitleOrDescriptionAndStatus(searchTerm, CourseStatus.PUBLISHED,
                    pageable);
        }

        return courses.map(this::mapToCourseResponse);
    }

    /**
     * Récupère un cours publié par ID avec ses chapitres et leçons
     */
    public CourseResponse getPublishedCourseById(String courseId) {
        log.info("📖 Récupération du cours publié: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new RuntimeException("Ce cours n'est pas encore publié");
        }

        CourseResponse response = mapToCourseResponse(course);

        // Ajouter les chapitres et leçons
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByOrderIndex(courseId);
        List<ChapterResponse> chapterResponses = chapters.stream()
                .map(this::mapToChapterResponse)
                .collect(Collectors.toList());

        response.setChapters(chapterResponses);

        return response;
    }

    /**
     * Récupère les cours les plus populaires (par nombre d'étudiants)
     */
    public List<CourseResponse> getPopularCourses(int limit) {
        log.info("🌟 Récupération des {} cours les plus populaires", limit);

        Pageable pageable = PageRequest.of(0, limit);
        List<Course> courses = courseRepository.findTopByStatusOrderByStudentsDesc(CourseStatus.PUBLISHED, pageable);

        return courses.stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les cours les mieux notés
     */
    public List<CourseResponse> getTopRatedCourses(int limit) {
        log.info("⭐ Récupération des {} cours les mieux notés", limit);

        Pageable pageable = PageRequest.of(0, limit);
        List<Course> courses = courseRepository.findTopByStatusOrderByRatingDesc(CourseStatus.PUBLISHED, pageable);

        return courses.stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère les derniers cours ajoutés
     */
    public List<CourseResponse> getLatestCourses(int limit) {
        log.info("🆕 Récupération des {} derniers cours", limit);

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Course> coursePage = courseRepository.findByStatus(CourseStatus.PUBLISHED, pageable);

        return coursePage.getContent().stream()
                .map(this::mapToCourseResponse)
                .collect(Collectors.toList());
    }

    /**
     * Compte le nombre total de cours publiés
     */
    public long countPublishedCourses() {
        return courseRepository.countByStatus(CourseStatus.PUBLISHED);
    }

    /**
     * Mappe un Course vers CourseResponse
     */
    private CourseResponse mapToCourseResponse(Course course) {
        CourseResponse response = modelMapper.map(course, CourseResponse.class);

        // Ajouter le nom de la catégorie
        if (course.getCategoryId() != null) {
            categoryRepository.findById(course.getCategoryId())
                    .ifPresent(category -> response.setCategoryName(category.getName()));
        }

        // 🆕 NOUVEAUTÉ : Calculer dynamiquement le nombre d'étudiants inscrits
        long enrolledStudents = userProgressRepository.countDistinctUsersByCourseId(course.getId());
        response.setStudents((int) enrolledStudents);

        log.debug("📊 Cours {}: {} étudiants inscrits calculés dynamiquement",
                course.getId(), enrolledStudents);

        return response;
    }

    /**
     * Mappe un Chapter vers ChapterResponse avec ses leçons
     */
    private ChapterResponse mapToChapterResponse(Chapter chapter) {
        ChapterResponse response = modelMapper.map(chapter, ChapterResponse.class);

        // Ajouter les leçons
        List<Lesson> lessons = lessonRepository.findByChapterIdOrderByOrderIndex(chapter.getId());
        List<LessonResponse> lessonResponses = lessons.stream()
                .map(lesson -> modelMapper.map(lesson, LessonResponse.class))
                .collect(Collectors.toList());

        response.setLessons(lessonResponses);

        return response;
    }
}