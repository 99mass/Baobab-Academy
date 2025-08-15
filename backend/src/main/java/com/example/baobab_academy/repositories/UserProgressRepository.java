package com.example.baobab_academy.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.baobab_academy.models.UserProgress;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends MongoRepository<UserProgress, String> {
    Optional<UserProgress> findByUserIdAndLessonId(String userId, String lessonId);

    List<UserProgress> findByUserIdAndCourseId(String userId, String courseId);

    List<UserProgress> findByUserId(String userId);

    long countByUserIdAndCourseIdAndIsCompleted(String userId, String courseId, Boolean isCompleted);
}
