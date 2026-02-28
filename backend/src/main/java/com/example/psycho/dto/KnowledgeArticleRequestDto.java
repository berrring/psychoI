package com.example.psycho.dto;

import com.example.psycho.model.KnowledgeCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record KnowledgeArticleRequestDto(
        @NotBlank String slug,
        @NotBlank String title,
        String summary,
        @NotBlank String content,
        @NotNull KnowledgeCategory category,
        String tags,
        Boolean published
) {
}

