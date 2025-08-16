package com.example.baobab_academy.controllers;

import com.example.baobab_academy.dtos.ApiResponse;
import com.example.baobab_academy.dtos.ChapterCreateRequest;
import com.example.baobab_academy.dtos.CourseCreateRequest;
import com.example.baobab_academy.dtos.CourseResponse;
import com.example.baobab_academy.dtos.CourseUpdateRequest;
import com.example.baobab_academy.dtos.LessonCreateRequest;
import com.example.baobab_academy.models.Chapter;
import com.example.baobab_academy.models.Lesson;
import com.example.baobab_academy.models.User;
import com.example.baobab_academy.services.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Course Management", description = "Gestion des cours (Admin uniquement)")
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Créer un nouveau cours")
    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CourseCreateRequest request,
            Authentication authentication) {
        
        log.info("🎓 Création d'un cours par l'admin: {}", authentication.getName());
        log.info("📝 Données reçues: {}", request);
        
        try {
            String instructorId = getUserIdFromAuthentication(authentication);
            log.info("👤 ID instructeur: {}", instructorId);
            
            CourseResponse course = courseService.createCourse(request, instructorId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Cours créé avec succès", course));
                    
        } catch (Exception e) {
            log.error("❌ Erreur lors de la création du cours: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de la création du cours: " + e.getMessage()));
        }
    }

    @Operation(summary = "Test endpoint")
    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> test() {
        log.info("🧪 Test endpoint appelé");
        return ResponseEntity.ok(ApiResponse.success("API fonctionne correctement", "Hello World"));
    }

    @Operation(summary = "Upload l'image de couverture d'un cours")
    @PostMapping("/{courseId}/cover-image")
    public ResponseEntity<ApiResponse<CourseResponse>> uploadCourseImage(
            @PathVariable String courseId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        log.info("📷 Upload image pour le cours: {}", courseId);
        
        try {
            String instructorId = getUserIdFromAuthentication(authentication);
            CourseResponse course = courseService.uploadCourseImage(courseId, file, instructorId);
            
            return ResponseEntity.ok(ApiResponse.success("Image uploadée avec succès", course));
            
        } catch (IOException e) {
            log.error("❌ Erreur lors de l'upload de l'image: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de l'upload: " + e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Erreur: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Mettre à jour un cours")
    @PutMapping("/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable String courseId,
            @Valid @RequestBody CourseUpdateRequest request,
            Authentication authentication) {
        
        log.info("✏️ Mise à jour du cours: {}", courseId);
        
        try {
            String instructorId = getUserIdFromAuthentication(authentication);
            CourseResponse course = courseService.updateCourse(courseId, request, instructorId);
            
            return ResponseEntity.ok(ApiResponse.success("Cours mis à jour avec succès", course));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la mise à jour: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Ajouter un chapitre à un cours")
    @PostMapping("/{courseId}/chapters")
    public ResponseEntity<ApiResponse<Chapter>> addChapter(
            @PathVariable String courseId,
            @Valid @RequestBody ChapterCreateRequest request,
            Authentication authentication) {
        
        log.info("📚 Ajout d'un chapitre au cours: {}", courseId);
        
        try {
            String instructorId = getUserIdFromAuthentication(authentication);
            Chapter chapter = courseService.addChapterToCourse(courseId, request, instructorId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Chapitre ajouté avec succès", chapter));
                    
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'ajout du chapitre: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Ajouter une leçon à un chapitre")
    @PostMapping("/chapters/{chapterId}/lessons")
    public ResponseEntity<ApiResponse<Lesson>> addLesson(
            @PathVariable String chapterId,
            @Valid @RequestBody LessonCreateRequest request,
            Authentication authentication) {
        
        log.info("📖 Ajout d'une leçon au chapitre: {}", chapterId);
        
        try {
            String instructorId = getUserIdFromAuthentication(authentication);
            Lesson lesson = courseService.addLessonToChapter(chapterId, request, instructorId);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Leçon ajoutée avec succès", lesson));
                    
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'ajout de la leçon: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Upload une vidéo pour une leçon")
    @PostMapping("/lessons/{lessonId}/video")
    public ResponseEntity<ApiResponse<Lesson>> uploadLessonVideo(
            @PathVariable String lessonId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {
        
        log.info("🎥 Upload vidéo pour la leçon: {}", lessonId);
        
        try {
            String instructorId = getUserIdFromAuthentication(authentication);
            Lesson lesson = courseService.uploadLessonVideo(lessonId, file, instructorId);
            
            return ResponseEntity.ok(ApiResponse.success("Vidéo uploadée avec succès", lesson));
            
        } catch (IOException e) {
            log.error("❌ Erreur lors de l'upload de la vidéo: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Erreur lors de l'upload: " + e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Erreur: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Publier un cours")
    @PostMapping("/{courseId}/publish")
    public ResponseEntity<ApiResponse<CourseResponse>> publishCourse(
            @PathVariable String courseId,
            Authentication authentication) {
        
        log.info("🚀 Publication du cours: {}", courseId);
        
        try {
            String instructorId = getUserIdFromAuthentication(authentication);
            CourseResponse course = courseService.publishCourse(courseId, instructorId);
            
            return ResponseEntity.ok(ApiResponse.success("Cours publié avec succès", course));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la publication: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Récupérer les cours de l'instructeur")
    @GetMapping("/my-courses")
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> getMyCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            Authentication authentication) {
        
        try {
            String instructorId = getUserIdFromAuthentication(authentication);
            
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<CourseResponse> courses = courseService.getInstructorCourses(instructorId, pageable);
            
            return ResponseEntity.ok(ApiResponse.success("Cours récupérés avec succès", courses));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des cours: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Récupérer un cours pour édition")
    @GetMapping("/{courseId}/edit")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseForEditing(
            @PathVariable String courseId,
            Authentication authentication) {
        
        try {
            String instructorId = getUserIdFromAuthentication(authentication);
            CourseResponse course = courseService.getCourseForEditing(courseId, instructorId);
            
            return ResponseEntity.ok(ApiResponse.success("Cours récupéré avec succès", course));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération du cours: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @Operation(summary = "Supprimer un cours")
    @DeleteMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(
            @PathVariable String courseId,
            Authentication authentication) {
        
        log.info("🗑️ Suppression du cours: {}", courseId);
        
        try {
            String instructorId = getUserIdFromAuthentication(authentication);
            courseService.deleteCourse(courseId, instructorId);
            
            return ResponseEntity.ok(ApiResponse.success("Cours supprimé avec succès", null));
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de la suppression: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Extrait l'ID utilisateur depuis l'authentification
     */
    private String getUserIdFromAuthentication(Authentication authentication) {
        log.info("🔍 Extraction de l'ID utilisateur de l'authentification");
        log.info("📋 Nom d'authentification: {}", authentication.getName());
        log.info("📋 Principal: {}", authentication.getPrincipal());
        log.info("📋 Type du principal: {}", authentication.getPrincipal().getClass().getName());
        
        // Assuming the authentication principal contains the User object
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            log.info("✅ Utilisateur trouvé: {}", user.getId());
            return user.getId();
        }
        
        // Fallback: assuming the name is the user ID or email
        log.warn("⚠️ Fallback: utilisation du nom d'authentification comme ID");
        return authentication.getName();
    }
}