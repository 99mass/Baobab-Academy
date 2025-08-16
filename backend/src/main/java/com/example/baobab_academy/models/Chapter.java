package com.example.baobab_academy.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "chapters")
public class Chapter {
    @Id
    private String id;

    @NotBlank(message = "Le titre du chapitre est obligatoire")
    @Size(min = 2, max = 200, message = "Le titre doit contenir entre 2 et 200 caractères")
    private String title;

    @NotNull(message = "Le cours est obligatoire")
    private String courseId; // Référence vers Course

    @NotNull(message = "L'ordre est obligatoire")
    @Min(value = 1, message = "L'ordre doit être supérieur à 0")
    private Integer orderIndex;

    @CreatedDate
    private LocalDateTime createdAt;

    public Chapter(String title, String courseId, Integer orderIndex) {
        this.title = title;
        this.courseId = courseId;
        this.orderIndex = orderIndex;
    }
}