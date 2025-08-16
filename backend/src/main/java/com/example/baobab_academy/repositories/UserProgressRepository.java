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
    
    // Trouver les leçons complétées par un utilisateur pour un cours
    List<UserProgress> findByUserIdAndCourseIdAndIsCompletedTrue(String userId, String courseId);
    
    // Compter les leçons complétées par un utilisateur pour un cours
    long countByUserIdAndCourseIdAndIsCompletedTrue(String userId, String courseId);
    
    // Supprimer la progression pour une leçon (utile lors de la suppression d'une leçon)
    void deleteByLessonId(String lessonId);
    
    // Supprimer la progression pour un cours (utile lors de la suppression d'un cours)
    void deleteByCourseId(String courseId);
    
    // Supprimer la progression d'un utilisateur pour un cours
    void deleteByUserIdAndCourseId(String userId, String courseId);
    
    // Vérifier si un utilisateur a une progression pour un cours
    boolean existsByUserIdAndCourseId(String userId, String courseId);
    
    // Trouver les utilisateurs qui ont progressé dans un cours
    @Query(value = "{'courseId': ?0}", fields = "{'userId': 1}")
    List<UserProgress> findDistinctUserIdByCourseId(String courseId);
    
    // Compter le nombre d'utilisateurs uniques pour un cours
    @Query(value = "{'courseId': ?0}", count = true)
    long countDistinctUserIdByCourseId(String courseId);
}