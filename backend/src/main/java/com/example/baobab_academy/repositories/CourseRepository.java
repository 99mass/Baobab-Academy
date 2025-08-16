package com.example.baobab_academy.repositories;

import com.example.baobab_academy.models.Course;
import com.example.baobab_academy.models.enums.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    
    // Trouver les cours par instructeur
    Page<Course> findByInstructorId(String instructorId, Pageable pageable);
    List<Course> findByInstructorId(String instructorId);
    
    // Trouver les cours par statut
    Page<Course> findByStatus(CourseStatus status, Pageable pageable);
    List<Course> findByStatus(CourseStatus status);
    
    // Trouver les cours par catégorie
    Page<Course> findByCategoryId(String categoryId, Pageable pageable);
    List<Course> findByCategoryId(String categoryId);
    
    // Trouver les cours publiés par catégorie
    Page<Course> findByCategoryIdAndStatus(String categoryId, CourseStatus status, Pageable pageable);
    
    // Recherche de cours par titre (insensible à la casse)
    @Query("{'title': {$regex: ?0, $options: 'i'}, 'status': ?1}")
    Page<Course> findByTitleContainingIgnoreCaseAndStatus(String title, CourseStatus status, Pageable pageable);
    
    // Recherche générale (titre ou description)
    @Query("{ $and: [ " +
           "{ $or: [ {'title': {$regex: ?0, $options: 'i'}}, {'description': {$regex: ?0, $options: 'i'}} ] }, " +
           "{ 'status': ?1 } ] }")
    Page<Course> searchByTitleOrDescriptionAndStatus(String searchTerm, CourseStatus status, Pageable pageable);
    
    // Compter les cours par instructeur
    long countByInstructorId(String instructorId);
    
    // Compter les cours par statut
    long countByStatus(CourseStatus status);
    
    // Vérifier si un cours existe et appartient à un instructeur
    boolean existsByIdAndInstructorId(String id, String instructorId);
    
    // Trouver les cours les plus populaires (par nombre d'étudiants)
    @Query("{'status': ?0}")
    List<Course> findTopByStatusOrderByStudentsDesc(CourseStatus status, Pageable pageable);
    
    // Trouver les cours les mieux notés
    @Query("{'status': ?0}")
    List<Course> findTopByStatusOrderByRatingDesc(CourseStatus status, Pageable pageable);
}