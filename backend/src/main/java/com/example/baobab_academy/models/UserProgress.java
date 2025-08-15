package com.example.baobab_academy.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_progress")
@CompoundIndex(name = "user_lesson_unique", def = "{'userId': 1, 'lessonId': 1}", unique = true)
public class UserProgress {
    @Id
    private String id;

    @NotNull(message = "L'utilisateur est obligatoire")
    private String userId; 

    @NotNull(message = "Le cours est obligatoire")
    private String courseId; 

    @NotNull(message = "La leçon est obligatoire")
    private String lessonId; 

    private Boolean isCompleted = false;

    private LocalDateTime completedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    public UserProgress(String userId, String courseId, String lessonId) {
        this.userId = userId;
        this.courseId = courseId;
        this.lessonId = lessonId;
    }

    public void markAsCompleted() {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
    }
}