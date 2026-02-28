package com.example.psycho.service.impl;

import com.example.psycho.dto.KnowledgeArticleRequestDto;
import com.example.psycho.dto.KnowledgeArticleResponseDto;
import com.example.psycho.dto.KnowledgeArticleUpdateDto;
import com.example.psycho.model.KnowledgeArticleEntity;
import com.example.psycho.model.UserEntity;
import com.example.psycho.mapper.KnowledgeArticleMapper;
import com.example.psycho.model.AuditAction;
import com.example.psycho.model.KnowledgeCategory;
import com.example.psycho.repository.KnowledgeArticleRepository;
import com.example.psycho.service.AuditService;
import com.example.psycho.service.IdentityService;
import com.example.psycho.service.KnowledgeArticleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class KnowledgeArticleServiceImpl implements KnowledgeArticleService {
    private final KnowledgeArticleRepository knowledgeArticleRepository;
    private final KnowledgeArticleMapper knowledgeArticleMapper;
    private final IdentityService identityService;
    private final AuditService auditService;

    public KnowledgeArticleServiceImpl(KnowledgeArticleRepository knowledgeArticleRepository,
                                       KnowledgeArticleMapper knowledgeArticleMapper,
                                       IdentityService identityService,
                                       AuditService auditService) {
        this.knowledgeArticleRepository = knowledgeArticleRepository;
        this.knowledgeArticleMapper = knowledgeArticleMapper;
        this.identityService = identityService;
        this.auditService = auditService;
    }

    @Override
    public KnowledgeArticleResponseDto create(KnowledgeArticleRequestDto request, String actorEmail) {
        if (knowledgeArticleRepository.findBySlug(request.slug()).isPresent()) {
            throw new IllegalArgumentException("Article with this slug already exists");
        }

        UserEntity actor = identityService.requireByEmail(actorEmail);

        KnowledgeArticleEntity article = new KnowledgeArticleEntity();
        article.setSlug(request.slug());
        article.setTitle(request.title());
        article.setSummary(request.summary());
        article.setContent(request.content());
        article.setCategory(request.category());
        article.setTags(request.tags());
        article.setAuthor(actor);

        boolean publish = request.published() != null && request.published();
        article.setPublished(publish);
        if (publish) {
            article.setPublishedAt(LocalDateTime.now());
        }

        KnowledgeArticleEntity saved = knowledgeArticleRepository.save(article);
        auditService.log("knowledge_articles", saved.getId(), AuditAction.CREATE, actor, "Article created: " + saved.getSlug());

        return knowledgeArticleMapper.toDto(saved);
    }

    @Override
    public KnowledgeArticleResponseDto update(Long id, KnowledgeArticleUpdateDto request, String actorEmail) {
        UserEntity actor = identityService.requireByEmail(actorEmail);
        KnowledgeArticleEntity article = knowledgeArticleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Article not found by id: " + id));

        if (request.title() != null) {
            article.setTitle(request.title());
        }
        if (request.summary() != null) {
            article.setSummary(request.summary());
        }
        if (request.content() != null) {
            article.setContent(request.content());
        }
        if (request.category() != null) {
            article.setCategory(request.category());
        }
        if (request.tags() != null) {
            article.setTags(request.tags());
        }

        if (request.published() != null) {
            article.setPublished(request.published());
            article.setPublishedAt(request.published() ? LocalDateTime.now() : null);
        }

        KnowledgeArticleEntity saved = knowledgeArticleRepository.save(article);
        auditService.log("knowledge_articles", saved.getId(), AuditAction.UPDATE, actor, "Article updated");

        return knowledgeArticleMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public KnowledgeArticleResponseDto getBySlug(String slug) {
        KnowledgeArticleEntity article = knowledgeArticleRepository.findBySlug(slug)
                .orElseThrow(() -> new EntityNotFoundException("Article not found by slug: " + slug));

        if (!article.isPublished()) {
            throw new EntityNotFoundException("Article is not published");
        }

        return knowledgeArticleMapper.toDto(article);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeArticleResponseDto> searchPublished(String query, KnowledgeCategory category, Pageable pageable) {
        String normalizedQuery = (query == null || query.isBlank()) ? null : query.trim();

        if (normalizedQuery == null) {
            if (category == null) {
                return knowledgeArticleRepository.findByPublishedTrueOrderByPublishedAtDescCreatedAtDesc(pageable)
                        .map(knowledgeArticleMapper::toDto);
            }

            return knowledgeArticleRepository.findByPublishedTrueAndCategoryOrderByPublishedAtDescCreatedAtDesc(category, pageable)
                    .map(knowledgeArticleMapper::toDto);
        }

        return knowledgeArticleRepository.searchPublished(normalizedQuery, category, pageable)
                .map(knowledgeArticleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KnowledgeArticleResponseDto> getAll(Pageable pageable) {
        return knowledgeArticleRepository.findAll(pageable)
                .map(knowledgeArticleMapper::toDto);
    }
}

