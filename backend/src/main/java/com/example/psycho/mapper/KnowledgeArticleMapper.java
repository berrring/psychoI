package com.example.psycho.mapper;

import com.example.psycho.dto.KnowledgeArticleResponseDto;
import com.example.psycho.model.KnowledgeArticleEntity;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeArticleMapper {
    public KnowledgeArticleResponseDto toDto(KnowledgeArticleEntity article) {
        Long authorId = article.getAuthor() != null ? article.getAuthor().getId() : null;
        String authorName = article.getAuthor() != null ? article.getAuthor().getName() : null;

        return new KnowledgeArticleResponseDto(
                article.getId(),
                article.getSlug(),
                article.getTitle(),
                article.getSummary(),
                article.getContent(),
                article.getCategory(),
                article.getTags(),
                article.isPublished(),
                authorId,
                authorName,
                article.getPublishedAt(),
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }
}

