package com.example.psycho.dto;

import com.example.psycho.model.KnowledgeCategory;

public record KnowledgeArticleUpdateDto(
        String title,
        String summary,
        String content,
        KnowledgeCategory category,
        String tags,
        Boolean published
) {
}

