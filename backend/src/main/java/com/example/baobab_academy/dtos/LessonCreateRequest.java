package com.example.baobab_academy.dtos;

import com.example.baobab_academy.models.enums.ContentType;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

@Data
public class LessonCreateRequest {
    @NotBlank(message = "Le titre de la leçon est obligatoire")
    @Size(min = 2, max = 200, message = "Le titre doit contenir entre 2 et 200 caractères")
    private String title;

    private String content;

    @NotNull(message = "Le type de contenu est obligatoire")
    private ContentType contentType;

    // 🆕 POUR LES VIDÉOS : Soit URL externe, soit upload local (géré séparément)
    private String videoUrl; // YouTube, Vimeo, etc.

    @Min(value = 0, message = "L'ordre ne peut pas être négatif")
    private Integer orderIndex = 0; // 0 = ajouter à la fin
}