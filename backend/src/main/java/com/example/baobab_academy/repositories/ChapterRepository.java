package com.example.baobab_academy.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.baobab_academy.models.Chapter;

import java.util.List;

@Repository
public interface ChapterRepository extends MongoRepository<Chapter, String> {
    List<Chapter> findByCourseIdOrderByOrderIndex(String courseId);
    long countByCourseId(String courseId);
}