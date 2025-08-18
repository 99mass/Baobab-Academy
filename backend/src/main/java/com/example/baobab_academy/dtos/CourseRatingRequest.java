package com.example.baobab_academy.dtos;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

@Data
public class CourseRatingRequest {
    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note minimum est 1")
    @Max(value = 5, message = "La note maximum est 5")
    private Integer rating;

    @Size(max = 500, message = "Le commentaire ne peut pas dépasser 500 caractères")
    private String comment;
}