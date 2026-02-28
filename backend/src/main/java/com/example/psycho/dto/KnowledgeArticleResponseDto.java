package com.example.psycho.dto;

import com.example.psycho.model.KnowledgeCategory;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record KnowledgeArticleResponseDto(
        Long id,
        String slug,
        String title,
        String summary,
        String content,
        KnowledgeCategory category,
        String tags,
        boolean published,
        Long authorId,
        String authorName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime publishedAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {
}

