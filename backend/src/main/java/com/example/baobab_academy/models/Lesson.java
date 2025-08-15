package com.example.baobab_academy.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.baobab_academy.models.enums.ContentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "lessons")
public class Lesson {
    @Id
    private String id;

    @NotBlank(message = "Le titre de la leçon est obligatoire")
    @Size(min = 2, max = 200, message = "Le titre doit contenir entre 2 et 200 caractères")
    private String title;

    private String content; // Contenu texte/HTML

    @NotNull(message = "Le type de contenu est obligatoire")
    private ContentType contentType;

    private String videoUrl; // URL YouTube/Vimeo pour les vidéos

    @NotNull(message = "Le chapitre est obligatoire")
    private String chapterId; // Référence vers Chapter

    @NotNull(message = "L'ordre est obligatoire")
    @Min(value = 1, message = "L'ordre doit être supérieur à 0")
    private Integer orderIndex;

    @CreatedDate
    private LocalDateTime createdAt;

    public Lesson(String title, String content, ContentType contentType, String chapterId, Integer orderIndex) {
        this.title = title;
        this.content = content;
        this.contentType = contentType;
        this.chapterId = chapterId;
        this.orderIndex = orderIndex;
    }
}
