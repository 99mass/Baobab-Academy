package com.example.baobab_academy.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.baobab_academy.models.enums.CourseLevel;
import com.example.baobab_academy.models.enums.CourseStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "courses")
public class Course {
    @Id
    private String id;

    @NotBlank(message = "Le titre du cours est obligatoire")
    @Size(min = 5, max = 200, message = "Le titre doit contenir entre 5 et 200 caractères")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, max = 1000, message = "La description doit contenir entre 10 et 1000 caractères")
    private String description;

    private String coverImage; 

    @NotNull(message = "La catégorie est obligatoire")
    private String categoryId;

    @NotNull(message = "L'instructeur est obligatoire")
    private String instructorId;

    @NotNull(message = "Le niveau est obligatoire")
    private CourseLevel level;

    @NotBlank(message = "La durée est obligatoire")
    private String duration; // Ex: "6 mois", "3 semaines"

    @Builder.Default
    private Integer students = 0; 

    @DecimalMin(value = "0.0", message = "La note doit être positive")
    @Builder.Default
    private Double rating = 0.0; // Note moyenne sur 5

    @Builder.Default
    private CourseStatus status = CourseStatus.DRAFT;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}