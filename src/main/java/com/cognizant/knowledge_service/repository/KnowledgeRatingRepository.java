package com.cognizant.knowledge_service.repository;

import com.cognizant.knowledge_service.entity.KnowledgeRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KnowledgeRatingRepository extends JpaRepository<KnowledgeRating, UUID> {

    Optional<KnowledgeRating> findByArticleArticleIdAndUserId(UUID articleId, UUID userId);

    Page<KnowledgeRating> findByArticleArticleId(UUID articleId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM KnowledgeRating r WHERE r.article.articleId = :articleId")
    Optional<Double> findAverageRatingByArticleId(@Param("articleId") UUID articleId);

    @Query("SELECT COUNT(r) FROM KnowledgeRating r WHERE r.article.articleId = :articleId")
    long countByArticleId(@Param("articleId") UUID articleId);

    boolean existsByArticleArticleIdAndUserId(UUID articleId, UUID userId);
}
