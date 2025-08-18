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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "course_ratings")
@CompoundIndex(def = "{'courseId': 1, 'userId': 1}", unique = true)
public class CourseRating {
    @Id
    private String id;

    @NotNull(message = "L'ID du cours est obligatoire")
    private String courseId;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private String userId;

    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note minimum est 1")
    @Max(value = 5, message = "La note maximum est 5")
    private Integer rating;

    @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
    private String comment;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}