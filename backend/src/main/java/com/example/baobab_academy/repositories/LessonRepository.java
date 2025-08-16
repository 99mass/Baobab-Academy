package com.example.baobab_academy.repositories;

import com.example.baobab_academy.models.Lesson;
import com.example.baobab_academy.models.enums.ContentType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends MongoRepository<Lesson, String> {
    
    // Trouver les leçons d'un chapitre, ordonnées par orderIndex
    List<Lesson> findByChapterIdOrderByOrderIndex(String chapterId);
    
    // Compter les leçons d'un chapitre
    long countByChapterId(String chapterId);
    
    // Supprimer toutes les leçons d'un chapitre
    void deleteByChapterId(String chapterId);
    
    // Trouver les leçons par type de contenu
    List<Lesson> findByChapterIdAndContentType(String chapterId, ContentType contentType);
    
    // Vérifier si une leçon existe pour un chapitre
    boolean existsByChapterIdAndId(String chapterId, String id);
    
    // Trouver la leçon avec l'ordre le plus élevé pour un chapitre
    Lesson findFirstByChapterIdOrderByOrderIndexDesc(String chapterId);
    
    // Trouver toutes les leçons d'un cours (via les chapitres)
    @Query("{'chapterId': {$in: ?0}}")
    List<Lesson> findByChapterIdIn(List<String> chapterIds);
    
    // Compter le nombre total de leçons pour un cours
    @Query(value = "{'chapterId': {$in: ?0}}", count = true)
    long countByChapterIdIn(List<String> chapterIds);
}
