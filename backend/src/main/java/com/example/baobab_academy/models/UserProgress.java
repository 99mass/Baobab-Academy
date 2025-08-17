package com.example.baobab_academy.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "user_progress")
@CompoundIndex(name = "user_lesson_idx", def = "{'userId': 1, 'lessonId': 1}", unique = true)
@CompoundIndex(name = "user_course_idx", def = "{'userId': 1, 'courseId': 1}")
public class UserProgress {
    @Id
    private String id;

    @NotNull(message = "L'utilisateur est obligatoire")
    private String userId;

    @NotNull(message = "Le cours est obligatoire")
    private String courseId;

    @NotNull(message = "La leçon est obligatoire")
    private String lessonId;

    @Builder.Default
    private boolean completed = false;

    @Builder.Default
    private int watchTimeSeconds = 0; // Temps de visionnage en secondes

    @Builder.Default
    private int progressPercentage = 0; // Pourcentage de progression (0-100)

    private LocalDateTime completedAt; // Date de completion

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public UserProgress(String userId, String courseId, String lessonId) {
        this.userId = userId;
        this.courseId = courseId;
        this.lessonId = lessonId;
        this.completed = false;
        this.watchTimeSeconds = 0;
        this.progressPercentage = 0;
    }

    public void markAsCompleted() {
        this.completed = true;
        this.progressPercentage = 100;
        this.completedAt = LocalDateTime.now();
    }
}