package com.example.baobab_academy.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.baobab_academy.models.Course;
import com.example.baobab_academy.models.enums.CourseStatus;

import java.util.List;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
    List<Course> findByStatus(CourseStatus status);

    List<Course> findByCategoryId(String categoryId);

    List<Course> findByInstructorId(String instructorId);

    @Query("{'title': {$regex: ?0, $options: 'i'}}")
    List<Course> findByTitleContainingIgnoreCase(String title);

    @Query("{'status': ?0, 'categoryId': ?1}")
    List<Course> findByStatusAndCategoryId(CourseStatus status, String categoryId);
}
