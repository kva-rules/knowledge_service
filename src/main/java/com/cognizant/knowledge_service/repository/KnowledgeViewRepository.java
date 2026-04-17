package com.cognizant.knowledge_service.repository;

import com.cognizant.knowledge_service.entity.KnowledgeView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KnowledgeViewRepository extends JpaRepository<KnowledgeView, UUID> {

    @Query("SELECT COUNT(v) FROM KnowledgeView v WHERE v.article.articleId = :articleId")
    long countByArticleId(@Param("articleId") UUID articleId);

    boolean existsByArticleArticleIdAndUserId(UUID articleId, UUID userId);
}
