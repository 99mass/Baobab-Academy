package com.example.baobab_academy.dtos;

import com.example.baobab_academy.models.enums.CourseLevel;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class CourseCreateRequest {
    @NotBlank(message = "Le titre du cours est obligatoire")
    @Size(min = 5, max = 200, message = "Le titre doit contenir entre 5 et 200 caractères")
    private String title;

    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, max = 1000, message = "La description doit contenir entre 10 et 1000 caractères")
    private String description;

    @NotNull(message = "La catégorie est obligatoire")
    private String categoryId;

    @NotNull(message = "Le niveau est obligatoire")
    private CourseLevel level;

    @NotBlank(message = "La durée est obligatoire")
    private String duration;
}
