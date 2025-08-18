package com.example.baobab_academy.repositories;

import com.example.baobab_academy.models.CourseRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRatingRepository extends MongoRepository<CourseRating, String> {
    
    Optional<CourseRating> findByCourseIdAndUserId(String courseId, String userId);
    
    Page<CourseRating> findByCourseIdOrderByCreatedAtDesc(String courseId, Pageable pageable);
    
    List<CourseRating> findByCourseId(String courseId);
    
    long countByCourseId(String courseId);
    
    @Query("{ 'courseId': ?0 }")
    List<CourseRating> findAllByCourseId(String courseId);
    
    void deleteByCourseId(String courseId);
    
    void deleteByUserId(String userId);
}
