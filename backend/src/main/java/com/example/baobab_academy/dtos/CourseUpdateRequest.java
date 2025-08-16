package com.example.baobab_academy.dtos;

import com.example.baobab_academy.models.enums.CourseLevel;
import com.example.baobab_academy.models.enums.CourseStatus;
import lombok.Data;
import jakarta.validation.constraints.Size;

@Data
public class CourseUpdateRequest {
    @Size(min = 5, max = 200, message = "Le titre doit contenir entre 5 et 200 caractères")
    private String title;

    @Size(min = 10, max = 1000, message = "La description doit contenir entre 10 et 1000 caractères")
    private String description;

    private String categoryId;
    private CourseLevel level;
    private String duration;
    private CourseStatus status;
}
