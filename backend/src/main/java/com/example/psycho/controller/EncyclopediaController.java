package com.example.psycho.controller;

import com.example.psycho.dto.KnowledgeArticleRequestDto;
import com.example.psycho.dto.KnowledgeArticleResponseDto;
import com.example.psycho.dto.KnowledgeArticleUpdateDto;
import com.example.psycho.model.UserEntity;
import com.example.psycho.model.KnowledgeCategory;
import com.example.psycho.service.KnowledgeArticleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class EncyclopediaController {
    private final KnowledgeArticleService knowledgeArticleService;

    public EncyclopediaController(KnowledgeArticleService knowledgeArticleService) {
        this.knowledgeArticleService = knowledgeArticleService;
    }

    @GetMapping("/public/knowledge/articles")
    public ResponseEntity<Page<KnowledgeArticleResponseDto>> publicSearch(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) KnowledgeCategory category,
            Pageable pageable
    ) {
        return ResponseEntity.ok(knowledgeArticleService.searchPublished(query, category, pageable));
    }

    @GetMapping("/public/knowledge/articles/{slug}")
    public ResponseEntity<KnowledgeArticleResponseDto> publicBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(knowledgeArticleService.getBySlug(slug));
    }

    @GetMapping("/knowledge/articles")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR','PSYCHOLOGIST','RECEPTIONIST')")
    public ResponseEntity<Page<KnowledgeArticleResponseDto>> all(Pageable pageable) {
        return ResponseEntity.ok(knowledgeArticleService.getAll(pageable));
    }

    @PostMapping("/knowledge/articles")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR','PSYCHOLOGIST')")
    public ResponseEntity<KnowledgeArticleResponseDto> create(@Valid @RequestBody KnowledgeArticleRequestDto request,
                                                              @AuthenticationPrincipal UserEntity actor) {
        return ResponseEntity.ok(knowledgeArticleService.create(request, actor.getEmail()));
    }

    @PatchMapping("/knowledge/articles/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','DOCTOR','PSYCHOLOGIST')")
    public ResponseEntity<KnowledgeArticleResponseDto> update(@PathVariable Long id,
                                                              @RequestBody KnowledgeArticleUpdateDto request,
                                                              @AuthenticationPrincipal UserEntity actor) {
        return ResponseEntity.ok(knowledgeArticleService.update(id, request, actor.getEmail()));
    }
}

