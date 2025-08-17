package com.example.baobab_academy.dtos;

import com.example.baobab_academy.models.enums.ContentType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LessonResponse {
    private String id;
    private String title;
    private String content;
    private ContentType contentType;
    private String videoUrl;
    private String documentUrl;
    private String chapterId;
    private Integer orderIndex;
    private LocalDateTime createdAt;
}