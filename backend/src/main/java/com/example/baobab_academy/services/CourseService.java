package com.example.baobab_academy.services;

import com.example.baobab_academy.dtos.ChapterCreateRequest;
import com.example.baobab_academy.dtos.CourseCreateRequest;
import com.example.baobab_academy.dtos.CourseResponse;
import com.example.baobab_academy.dtos.CourseUpdateRequest;
import com.example.baobab_academy.dtos.LessonCreateRequest;
import com.example.baobab_academy.models.*;
import com.example.baobab_academy.models.enums.CourseStatus;
import com.example.baobab_academy.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final ChapterRepository chapterRepository;
    private final LessonRepository lessonRepository;
    private final UserProgressRepository userProgressRepository;
    private final CloudinaryService cloudinaryService;
    private final ModelMapper modelMapper;

    /**
     * Crée un nouveau cours
     */
    public CourseResponse createCourse(CourseCreateRequest request, String instructorId) {
        log.info("🎓 Création d'un nouveau cours: {}", request.getTitle());

        // Vérifier que la catégorie existe
        // Category category = categoryRepository.findById(request.getCategoryId())
        //         .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .categoryId(request.getCategoryId())
                .instructorId(instructorId)
                .level(request.getLevel())
                .duration(request.getDuration())
                .build(); // Les valeurs par défaut seront appliquées grâce à @Builder.Default

        Course savedCourse = courseRepository.save(course);
        log.info("✅ Cours créé avec l'ID: {}", savedCourse.getId());

        return mapToCourseResponse(savedCourse);
    }

    /**
     * Upload l'image de couverture d'un cours
     */
    public CourseResponse uploadCourseImage(String courseId, MultipartFile file, String instructorId) throws IOException {
        log.info("📷 Upload image pour le cours: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        // Vérifier que l'utilisateur est le créateur du cours
        if (!course.getInstructorId().equals(instructorId)) {
            throw new RuntimeException("Accès non autorisé à ce cours");
        }

        // Supprimer l'ancienne image si elle existe
        if (course.getCoverImage() != null) {
            String oldPublicId = cloudinaryService.extractPublicIdFromUrl(course.getCoverImage());
            if (oldPublicId != null) {
                cloudinaryService.deleteImage(oldPublicId);
            }
        }

        // Upload la nouvelle image
        CloudinaryService.CloudinaryUploadResult result = cloudinaryService.uploadCourseImage(file, courseId);
        
        course.setCoverImage(result.getSecureUrl());
        Course updatedCourse = courseRepository.save(course);

        log.info("✅ Image de cours uploadée: {}", result.getSecureUrl());
        return mapToCourseResponse(updatedCourse);
    }

    /**
     * Met à jour un cours
     */
    public CourseResponse updateCourse(String courseId, CourseUpdateRequest request, String instructorId) {
        log.info("✏️ Mise à jour du cours: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        // Vérifier que l'utilisateur est le créateur du cours
        if (!course.getInstructorId().equals(instructorId)) {
            throw new RuntimeException("Accès non autorisé à ce cours");
        }

        // Vérifier que la catégorie existe si elle a changé
        if (request.getCategoryId() != null && !request.getCategoryId().equals(course.getCategoryId())) {
            categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));
        }

        // Mettre à jour les champs
        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getCategoryId() != null) course.setCategoryId(request.getCategoryId());
        if (request.getLevel() != null) course.setLevel(request.getLevel());
        if (request.getDuration() != null) course.setDuration(request.getDuration());
        if (request.getStatus() != null) course.setStatus(request.getStatus());

        Course updatedCourse = courseRepository.save(course);
        log.info("✅ Cours mis à jour: {}", courseId);

        return mapToCourseResponse(updatedCourse);
    }

    /**
     * Ajoute un chapitre à un cours
     */
    public Chapter addChapterToCourse(String courseId, ChapterCreateRequest request, String instructorId) {
        log.info("📚 Ajout d'un chapitre au cours: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        // Vérifier que l'utilisateur est le créateur du cours
        if (!course.getInstructorId().equals(instructorId)) {
            throw new RuntimeException("Accès non autorisé à ce cours");
        }

        // Déterminer l'ordre du chapitre
        int orderIndex = request.getOrderIndex();
        if (orderIndex <= 0) {
            // Si pas d'ordre spécifié, ajouter à la fin
            List<Chapter> existingChapters = chapterRepository.findByCourseIdOrderByOrderIndex(courseId);
            orderIndex = existingChapters.size() + 1;
        }

        Chapter chapter = Chapter.builder()
                .title(request.getTitle())
                .courseId(courseId)
                .orderIndex(orderIndex)
                .build();

        Chapter savedChapter = chapterRepository.save(chapter);
        log.info("✅ Chapitre créé avec l'ID: {}", savedChapter.getId());

        return savedChapter;
    }

    /**
     * Ajoute une leçon à un chapitre
     */
    public Lesson addLessonToChapter(String chapterId, LessonCreateRequest request, String instructorId) {
        log.info("📖 Ajout d'une leçon au chapitre: {}", chapterId);

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new RuntimeException("Chapitre non trouvé"));

        // Vérifier que l'utilisateur est le créateur du cours
        Course course = courseRepository.findById(chapter.getCourseId())
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        if (!course.getInstructorId().equals(instructorId)) {
            throw new RuntimeException("Accès non autorisé à ce cours");
        }

        // Déterminer l'ordre de la leçon
        int orderIndex = request.getOrderIndex();
        if (orderIndex <= 0) {
            // Si pas d'ordre spécifié, ajouter à la fin
            List<Lesson> existingLessons = lessonRepository.findByChapterIdOrderByOrderIndex(chapterId);
            orderIndex = existingLessons.size() + 1;
        }

        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .contentType(request.getContentType())
                .videoUrl(request.getVideoUrl())
                .chapterId(chapterId)
                .orderIndex(orderIndex)
                .build();

        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("✅ Leçon créée avec l'ID: {}", savedLesson.getId());

        return savedLesson;
    }

    /**
     * Upload une vidéo pour une leçon
     */
    public Lesson uploadLessonVideo(String lessonId, MultipartFile file, String instructorId) throws IOException {
        log.info("🎥 Upload vidéo pour la leçon: {}", lessonId);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Leçon non trouvée"));

        // Vérifier l'autorisation
        Chapter chapter = chapterRepository.findById(lesson.getChapterId())
                .orElseThrow(() -> new RuntimeException("Chapitre non trouvé"));
        
        Course course = courseRepository.findById(chapter.getCourseId())
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        if (!course.getInstructorId().equals(instructorId)) {
            throw new RuntimeException("Accès non autorisé à ce cours");
        }

        // Supprimer l'ancienne vidéo si elle existe
        if (lesson.getVideoUrl() != null) {
            String oldPublicId = cloudinaryService.extractPublicIdFromUrl(lesson.getVideoUrl());
            if (oldPublicId != null) {
                cloudinaryService.deleteVideo(oldPublicId);
            }
        }

        // Upload la nouvelle vidéo
        CloudinaryService.CloudinaryUploadResult result = cloudinaryService.uploadLessonVideo(file, course.getId(), lessonId);
        
        lesson.setVideoUrl(result.getSecureUrl());
        Lesson updatedLesson = lessonRepository.save(lesson);

        log.info("✅ Vidéo de leçon uploadée: {}", result.getSecureUrl());
        return updatedLesson;
    }

    /**
     * Publie un cours (change le statut en PUBLISHED)
     */
    public CourseResponse publishCourse(String courseId, String instructorId) {
        log.info("🚀 Publication du cours: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        if (!course.getInstructorId().equals(instructorId)) {
            throw new RuntimeException("Accès non autorisé à ce cours");
        }

        // Vérifier que le cours a au moins un chapitre et une leçon
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByOrderIndex(courseId);
        if (chapters.isEmpty()) {
            throw new RuntimeException("Le cours doit avoir au moins un chapitre pour être publié");
        }

        boolean hasLessons = chapters.stream()
                .anyMatch(chapter -> !lessonRepository.findByChapterIdOrderByOrderIndex(chapter.getId()).isEmpty());
        
        if (!hasLessons) {
            throw new RuntimeException("Le cours doit avoir au moins une leçon pour être publié");
        }

        course.setStatus(CourseStatus.PUBLISHED);
        Course publishedCourse = courseRepository.save(course);

        log.info("✅ Cours publié: {}", courseId);
        return mapToCourseResponse(publishedCourse);
    }

    /**
     * Supprime un cours et toutes ses données associées
     */
    public void deleteCourse(String courseId, String instructorId) {
        log.info("🗑️ Suppression du cours: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        if (!course.getInstructorId().equals(instructorId)) {
            throw new RuntimeException("Accès non autorisé à ce cours");
        }

        // Supprimer l'image de couverture
        if (course.getCoverImage() != null) {
            String publicId = cloudinaryService.extractPublicIdFromUrl(course.getCoverImage());
            if (publicId != null) {
                cloudinaryService.deleteImage(publicId);
            }
        }

        // Supprimer toutes les leçons et leurs médias
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByOrderIndex(courseId);
        for (Chapter chapter : chapters) {
            List<Lesson> lessons = lessonRepository.findByChapterIdOrderByOrderIndex(chapter.getId());
            for (Lesson lesson : lessons) {
                // Supprimer les médias de la leçon
                if (lesson.getVideoUrl() != null) {
                    String videoPublicId = cloudinaryService.extractPublicIdFromUrl(lesson.getVideoUrl());
                    if (videoPublicId != null) {
                        cloudinaryService.deleteVideo(videoPublicId);
                    }
                }
                
                // Supprimer la progression des utilisateurs pour cette leçon
                userProgressRepository.deleteByLessonId(lesson.getId());
                
                // Supprimer la leçon
                lessonRepository.delete(lesson);
            }
            
            // Supprimer le chapitre
            chapterRepository.delete(chapter);
        }

        // Supprimer le cours
        courseRepository.delete(course);
        
        log.info("✅ Cours supprimé: {}", courseId);
    }

    /**
     * Récupère les cours d'un instructeur
     */
    public Page<CourseResponse> getInstructorCourses(String instructorId, Pageable pageable) {
        Page<Course> courses = courseRepository.findByInstructorId(instructorId, pageable);
        return courses.map(this::mapToCourseResponse);
    }

    public CourseResponse getCourseForEditing(String courseId, String instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        if (!course.getInstructorId().equals(instructorId)) {
            throw new RuntimeException("Accès non autorisé à ce cours");
        }

        CourseResponse response = mapToCourseResponse(course);

        // Ajouter les chapitres et leçons
        List<Chapter> chapters = chapterRepository.findByCourseIdOrderByOrderIndex(courseId);
        List<com.example.baobab_academy.dtos.ChapterResponse> chapterResponses = chapters.stream().map(chapter -> {
            com.example.baobab_academy.dtos.ChapterResponse chapterResponse = modelMapper.map(chapter, com.example.baobab_academy.dtos.ChapterResponse.class);
            List<Lesson> lessons = lessonRepository.findByChapterIdOrderByOrderIndex(chapter.getId());
            List<com.example.baobab_academy.dtos.LessonResponse> lessonResponses = lessons.stream()
                    .map(lesson -> modelMapper.map(lesson, com.example.baobab_academy.dtos.LessonResponse.class))
                    .collect(Collectors.toList());
            chapterResponse.setLessons(lessonResponses);
            return chapterResponse;
        }).collect(Collectors.toList());

        response.setChapters(chapterResponses);

        return response;
    }

    /**
     * Mappe un Course vers CourseResponse
     */
    private CourseResponse mapToCourseResponse(Course course) {
        CourseResponse response = modelMapper.map(course, CourseResponse.class);
        
        // Ajouter des informations supplémentaires si nécessaire
        if (course.getCategoryId() != null) {
            categoryRepository.findById(course.getCategoryId())
                    .ifPresent(category -> response.setCategoryName(category.getName()));
        }
        
        return response;
    }
}