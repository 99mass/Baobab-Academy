package com.example.baobab_academy.dtos;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChapterResponse {
    private String id;
    private String title;
    private String courseId;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private List<LessonResponse> lessons;
}
