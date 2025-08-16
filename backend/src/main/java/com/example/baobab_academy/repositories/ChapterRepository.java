package com.example.baobab_academy.repositories;

import com.example.baobab_academy.models.Chapter;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChapterRepository extends MongoRepository<Chapter, String> {
    
    // Trouver les chapitres d'un cours, ordonnés par orderIndex
    List<Chapter> findByCourseIdOrderByOrderIndex(String courseId);
    
    // Compter les chapitres d'un cours
    long countByCourseId(String courseId);
    
    // Supprimer tous les chapitres d'un cours
    void deleteByCourseId(String courseId);
    
    // Vérifier si un chapitre existe pour un cours
    boolean existsByCourseIdAndId(String courseId, String id);
    
    // Trouver le chapitre avec l'ordre le plus élevé pour un cours
    Chapter findFirstByCourseIdOrderByOrderIndexDesc(String courseId);
}