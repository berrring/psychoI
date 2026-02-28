package com.example.psycho.service;

import com.example.psycho.dto.KnowledgeArticleRequestDto;
import com.example.psycho.dto.KnowledgeArticleResponseDto;
import com.example.psycho.dto.KnowledgeArticleUpdateDto;
import com.example.psycho.model.KnowledgeCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface KnowledgeArticleService {
    KnowledgeArticleResponseDto create(KnowledgeArticleRequestDto request, String actorEmail);

    KnowledgeArticleResponseDto update(Long id, KnowledgeArticleUpdateDto request, String actorEmail);

    KnowledgeArticleResponseDto getBySlug(String slug);

    Page<KnowledgeArticleResponseDto> searchPublished(String query, KnowledgeCategory category, Pageable pageable);

    Page<KnowledgeArticleResponseDto> getAll(Pageable pageable);
}

