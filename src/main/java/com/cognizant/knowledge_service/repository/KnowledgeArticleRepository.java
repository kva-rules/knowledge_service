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

    // Postgres can't infer the type of an untyped NULL bind parameter against a UUID
    // column, and JPA's `:p IS NULL` lowers to a bare `? IS NULL`. We CAST each nullable
    // bind to its column's SQL type so Postgres knows what's coming. Without these casts
    // the query throws "could not determine data type of parameter $4" on every search
    // request that doesn't filter by categoryId. The keyword param is a String LIKE
    // expression and Postgres handles that one fine, so it's left as-is.
    // Native query with explicit column list (NOT `SELECT *`) — `*` from a join with
    // article_tags would expose two `article_id` columns and Hibernate's auto-discovery
    // throws NonUniqueDiscoveredSqlAliasException. The explicit projection keeps only
    // the knowledge_articles columns Hibernate needs to hydrate KnowledgeArticle.
    @Query(value = "SELECT DISTINCT ka.article_id, ka.content, ka.created_at, ka.created_by, " +
            "ka.deleted, ka.solution_id, ka.status, ka.ticket_id, ka.title, " +
            "ka.updated_at, ka.version, ka.visibility, ka.category_id " +
            "FROM knowledge_articles ka " +
            "LEFT JOIN article_tags at ON ka.article_id = at.article_id " +
            "LEFT JOIN knowledge_tags kt ON at.tag_id = kt.tag_id " +
            "WHERE ka.deleted = false " +
            "AND (CAST(:keyword AS text) IS NULL OR LOWER(ka.title) LIKE LOWER('%' || :keyword || '%') OR LOWER(ka.content) LIKE LOWER('%' || :keyword || '%')) " +
            "AND (CAST(:categoryId AS uuid) IS NULL OR ka.category_id = CAST(:categoryId AS uuid)) " +
            "AND (CAST(:tagName AS text) IS NULL OR kt.tag_name = :tagName) " +
            "AND (CAST(:status AS text) IS NULL OR ka.status = :status)",
           countQuery = "SELECT COUNT(DISTINCT ka.article_id) FROM knowledge_articles ka " +
                        "LEFT JOIN article_tags at ON ka.article_id = at.article_id " +
                        "LEFT JOIN knowledge_tags kt ON at.tag_id = kt.tag_id " +
                        "WHERE ka.deleted = false " +
                        "AND (CAST(:keyword AS text) IS NULL OR LOWER(ka.title) LIKE LOWER('%' || :keyword || '%') OR LOWER(ka.content) LIKE LOWER('%' || :keyword || '%')) " +
                        "AND (CAST(:categoryId AS uuid) IS NULL OR ka.category_id = CAST(:categoryId AS uuid)) " +
                        "AND (CAST(:tagName AS text) IS NULL OR kt.tag_name = :tagName) " +
                        "AND (CAST(:status AS text) IS NULL OR ka.status = :status)",
           nativeQuery = true)
    Page<KnowledgeArticle> searchArticles(
            @Param("keyword") String keyword,
            @Param("categoryId") UUID categoryId,
            @Param("tagName") String tagName,
            @Param("status") String status,
            Pageable pageable);

    @Query("SELECT COUNT(a) FROM KnowledgeArticle a WHERE a.category.categoryId = :categoryId AND a.deleted = false")
    long countByCategoryId(@Param("categoryId") UUID categoryId);
}



