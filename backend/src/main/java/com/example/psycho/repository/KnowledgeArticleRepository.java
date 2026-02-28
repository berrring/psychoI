package com.example.psycho.repository;

import com.example.psycho.model.KnowledgeArticleEntity;
import com.example.psycho.model.KnowledgeCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticleEntity, Long> {
    Optional<KnowledgeArticleEntity> findBySlug(String slug);
    Page<KnowledgeArticleEntity> findByPublishedTrueOrderByPublishedAtDescCreatedAtDesc(Pageable pageable);
    Page<KnowledgeArticleEntity> findByPublishedTrueAndCategoryOrderByPublishedAtDescCreatedAtDesc(KnowledgeCategory category, Pageable pageable);

    @Query("""
            select k from KnowledgeArticleEntity k
            where k.published = true
              and (
                    lower(k.title) like lower(concat('%', :query, '%'))
                    or lower(coalesce(k.summary, '')) like lower(concat('%', :query, '%'))
                    or lower(coalesce(k.tags, '')) like lower(concat('%', :query, '%'))
                  )
              and (:category is null or k.category = :category)
            order by k.publishedAt desc, k.createdAt desc
            """)
    Page<KnowledgeArticleEntity> searchPublished(
            @Param("query") String query,
            @Param("category") KnowledgeCategory category,
            Pageable pageable
    );
}

