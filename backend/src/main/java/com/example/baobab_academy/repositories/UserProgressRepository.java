package com.example.baobab_academy.repositories;

import com.example.baobab_academy.models.UserProgress;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends MongoRepository<UserProgress, String> {
    
    // Trouver la progression d'un utilisateur pour une leçon spécifique
    Optional<UserProgress> findByUserIdAndLessonId(String userId, String lessonId);
    
    // Trouver toute la progression d'un utilisateur pour un cours
    List<UserProgress> findByUserIdAndCourseId(String userId, String courseId);
    
    // Supprimer la progression pour une leçon (utile lors de suppression)
    void deleteByLessonId(String lessonId);
    
    // Supprimer la progression pour un cours complet
    void deleteByCourseId(String courseId);
    
    // Supprimer toute la progression d'un utilisateur
    void deleteByUserId(String userId);
    
    // Vérifier si un utilisateur a commencé un cours
    boolean existsByUserIdAndCourseId(String userId, String courseId);
    
    // Compter les leçons complétées par un utilisateur pour un cours
    long countByUserIdAndCourseIdAndCompletedTrue(String userId, String courseId);
    
    // Compter le nombre total de leçons d'un cours
    long countByCourseId(String courseId);
    
    // 🆕 NOUVEAUTÉ : Compter le nombre d'utilisateurs uniques inscrits à un cours
    @Query(value = "{'courseId': ?0}", count = true)
    long countDistinctUsersByCourseId(String courseId);
    
    // 🆕 ALTERNATIVE : Méthode plus précise pour compter les utilisateurs distincts
    @Query("{ 'courseId': ?0 }")
    List<UserProgress> findByCourseId(String courseId);
    
    // Vous pouvez aussi utiliser cette méthode dans le service pour compter manuellement
    default long countUniqueUsersByCourseId(String courseId) {
        return findByCourseId(courseId).stream()
                .map(UserProgress::getUserId)
                .distinct()
                .count();
    }
}