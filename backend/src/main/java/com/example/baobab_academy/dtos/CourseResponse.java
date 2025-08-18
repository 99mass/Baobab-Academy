package com.example.baobab_academy.dtos;

import com.example.baobab_academy.models.enums.CourseLevel;
import com.example.baobab_academy.models.enums.CourseStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CourseResponse {
    private String id;
    private String title;
    private String description;
    private String coverImage;
    private String categoryId;
    private String categoryName;
    private String instructorId;
    private CourseLevel level;
    private String duration;
    private Integer students;
    private Double rating;
    private CourseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ChapterResponse> chapters;
    private Long totalRatings;
}
