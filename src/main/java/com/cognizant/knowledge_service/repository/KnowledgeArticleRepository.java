package com.cognizant.knowledge_service.repository;

import com.cognizant.knowledge_service.entity.KnowledgeArticle;
import com.cognizant.knowledge_service.enums.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, UUID> {

    Page<KnowledgeArticle> findByCategoryCategoryIdAndDeletedFalse(UUID categoryId, Pageable pageable);

    Page<KnowledgeArticle> findByStatusAndDeletedFalse(ArticleStatus status, Pageable pageable);

    Page<KnowledgeArticle> findByDeletedFalse(Pageable pageable);

    Optional<KnowledgeArticle> findByArticleIdAndDeletedFalse(UUID articleId);

    List<KnowledgeArticle> findByTicketIdAndDeletedFalse(UUID ticketId);

    List<KnowledgeArticle> findBySolutionIdAndDeletedFalse(UUID solutionId);

    @Query("SELECT a FROM KnowledgeArticle a WHERE a.deleted = false AND " +
            "(LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<KnowledgeArticle> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT a FROM KnowledgeArticle a JOIN a.tags t WHERE a.deleted = false AND t.tagName = :tagName")
    Page<KnowledgeArticle> findByTagName(@Param("tagName") String tagName, Pageable pageable);

    @Query("SELECT a FROM KnowledgeArticle a LEFT JOIN a.tags t WHERE a.deleted = false AND " +
            "(:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR a.category.categoryId = :categoryId) AND " +
            "(:tagName IS NULL OR t.tagName = :tagName) AND " +
            "(:status IS NULL OR a.status = :status)")
    Page<KnowledgeArticle> searchArticles(
            @Param("keyword") String keyword,
            @Param("categoryId") UUID categoryId,
            @Param("tagName") String tagName,
            @Param("status") ArticleStatus status,
            Pageable pageable);

    @Query("SELECT COUNT(a) FROM KnowledgeArticle a WHERE a.category.categoryId = :categoryId AND a.deleted = false")
    long countByCategoryId(@Param("categoryId") UUID categoryId);
}



