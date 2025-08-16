package com.example.baobab_academy.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryResponse {
    private String id;
    private String name;
    private LocalDateTime createdAt;
}