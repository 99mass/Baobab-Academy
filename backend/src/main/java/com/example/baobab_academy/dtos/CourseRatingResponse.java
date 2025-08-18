package com.example.baobab_academy.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CourseRatingResponse {
    private String id;
    private String courseId;
    private String userId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Informations utilisateur (optionnel, pour l'affichage)
    private String userFirstName;
    private String userLastName;
}