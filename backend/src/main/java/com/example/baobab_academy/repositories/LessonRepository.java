package com.example.baobab_academy.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.baobab_academy.models.Lesson;

import java.util.List;

@Repository
public interface LessonRepository extends MongoRepository<Lesson, String> {
    List<Lesson> findByChapterIdOrderByOrderIndex(String chapterId);
    long countByChapterId(String chapterId);
}
