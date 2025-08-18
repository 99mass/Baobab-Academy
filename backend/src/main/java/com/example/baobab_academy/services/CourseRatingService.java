package com.example.baobab_academy.services;

import com.example.baobab_academy.dtos.CourseRatingRequest;
import com.example.baobab_academy.dtos.CourseRatingResponse;
import com.example.baobab_academy.dtos.RatingStatsResponse;
import com.example.baobab_academy.models.Course;
import com.example.baobab_academy.models.CourseRating;
import com.example.baobab_academy.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseRatingService {

    private final CourseRatingRepository courseRatingRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final ModelMapper modelMapper;

    /**
     * Noter un cours ou mettre à jour une note existante
     */
    public CourseRatingResponse rateCourse(String courseId, String userId, CourseRatingRequest request) {
        log.info("⭐ Notation du cours {} par l'utilisateur {}", courseId, userId);

        // Vérifier que le cours existe et est publié
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        if (course.getStatus() != com.example.baobab_academy.models.enums.CourseStatus.PUBLISHED) {
            throw new RuntimeException("Impossible de noter un cours non publié");
        }

        // Vérifier que l'utilisateur est inscrit au cours
        boolean isEnrolled = userProgressRepository.countByUserIdAndCourseId(userId, courseId) > 0;
        if (!isEnrolled) {
            throw new RuntimeException("Vous devez être inscrit au cours pour le noter");
        }

        // Vérifier si l'utilisateur a déjà noté ce cours
        CourseRating existingRating = courseRatingRepository.findByCourseIdAndUserId(courseId, userId)
                .orElse(null);

        CourseRating rating;
        if (existingRating != null) {
            // Mettre à jour la note existante
            existingRating.setRating(request.getRating());
            existingRating.setComment(request.getComment());
            rating = courseRatingRepository.save(existingRating);
            log.info("✅ Note mise à jour pour le cours {}", courseId);
        } else {
            // Créer une nouvelle note
            rating = CourseRating.builder()
                    .courseId(courseId)
                    .userId(userId)
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .build();
            rating = courseRatingRepository.save(rating);
            log.info("✅ Nouvelle note créée pour le cours {}", courseId);
        }

        // Mettre à jour la note moyenne du cours
        updateCourseAverageRating(courseId);

        return mapToResponse(rating);
    }

    /**
     * Récupérer la note d'un utilisateur pour un cours
     */
    @Transactional(readOnly = true)
    public CourseRatingResponse getUserRating(String courseId, String userId) {
        log.info("📊 Récupération de la note utilisateur {} pour le cours {}", userId, courseId);

        CourseRating rating = courseRatingRepository.findByCourseIdAndUserId(courseId, userId)
                .orElseThrow(() -> new RuntimeException("Aucune note trouvée"));

        return mapToResponse(rating);
    }

    /**
     * Récupérer toutes les notes d'un cours avec pagination
     */
    @Transactional(readOnly = true)
    public Page<CourseRatingResponse> getCourseRatings(String courseId, Pageable pageable) {
        log.info("📝 Récupération des notes du cours {} - Page: {}", courseId, pageable.getPageNumber());

        Page<CourseRating> ratings = courseRatingRepository.findByCourseIdOrderByCreatedAtDesc(courseId, pageable);
        
        return ratings.map(this::mapToResponseWithUserInfo);
    }

    /**
     * Obtenir les statistiques de notation d'un cours
     */
    @Transactional(readOnly = true)
    public RatingStatsResponse getCourseRatingStats(String courseId) {
        log.info("📈 Calcul des statistiques de notation pour le cours {}", courseId);

        List<CourseRating> ratings = courseRatingRepository.findByCourseId(courseId);

        if (ratings.isEmpty()) {
            return RatingStatsResponse.builder()
                    .averageRating(0.0)
                    .totalRatings(0L)
                    .ratingDistribution(Map.of(1, 0L, 2, 0L, 3, 0L, 4, 0L, 5, 0L))
                    .build();
        }

        // Calculer la moyenne
        double averageRating = ratings.stream()
                .mapToInt(CourseRating::getRating)
                .average()
                .orElse(0.0);

        // Calculer la distribution
        Map<Integer, Long> distribution = ratings.stream()
                .collect(Collectors.groupingBy(
                        CourseRating::getRating,
                        Collectors.counting()
                ));

        // S'assurer que toutes les étoiles sont représentées
        for (int i = 1; i <= 5; i++) {
            distribution.putIfAbsent(i, 0L);
        }

        return RatingStatsResponse.builder()
                .averageRating(averageRating)
                .totalRatings((long) ratings.size())
                .ratingDistribution(distribution)
                .build();
    }

    /**
     * Supprimer une note
     */
    public void deleteRating(String courseId, String userId) {
        log.info("🗑️ Suppression de la note du cours {} par l'utilisateur {}", courseId, userId);

        CourseRating rating = courseRatingRepository.findByCourseIdAndUserId(courseId, userId)
                .orElseThrow(() -> new RuntimeException("Note non trouvée"));

        courseRatingRepository.delete(rating);
        
        // Mettre à jour la note moyenne du cours
        updateCourseAverageRating(courseId);
        
        log.info("✅ Note supprimée");
    }

    /**
     * Met à jour la note moyenne d'un cours
     */
    private void updateCourseAverageRating(String courseId) {
        List<CourseRating> ratings = courseRatingRepository.findByCourseId(courseId);
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        if (ratings.isEmpty()) {
            course.setRating(0.0);
        } else {
            double averageRating = ratings.stream()
                    .mapToInt(CourseRating::getRating)
                    .average()
                    .orElse(0.0);
            course.setRating(Math.round(averageRating * 10.0) / 10.0); // Arrondir à 1 décimale
        }

        courseRepository.save(course);
        log.info("📊 Note moyenne du cours {} mise à jour: {}", courseId, course.getRating());
    }

    /**
     * Mapper vers CourseRatingResponse
     */
    private CourseRatingResponse mapToResponse(CourseRating rating) {
        return modelMapper.map(rating, CourseRatingResponse.class);
    }

    /**
     * Mapper vers CourseRatingResponse avec informations utilisateur
     */
    private CourseRatingResponse mapToResponseWithUserInfo(CourseRating rating) {
        CourseRatingResponse response = modelMapper.map(rating, CourseRatingResponse.class);
        
        // Ajouter les informations utilisateur (optionnel)
        userRepository.findById(rating.getUserId()).ifPresent(user -> {
            response.setUserFirstName(user.getFirstName());
            response.setUserLastName(user.getLastName());
        });
        
        return response;
    }
}
