package com.example.baobab_academy.dtos;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

@Data
public class ChapterCreateRequest {
    @NotBlank(message = "Le titre du chapitre est obligatoire")
    @Size(min = 2, max = 200, message = "Le titre doit contenir entre 2 et 200 caractères")
    private String title;

    @Min(value = 0, message = "L'ordre ne peut pas être négatif")
    private Integer orderIndex = 0; // 0 = ajouter à la fin
}
