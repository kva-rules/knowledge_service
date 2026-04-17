package com.cognizant.knowledge_service.repository;

import com.cognizant.knowledge_service.entity.KnowledgeVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KnowledgeVersionRepository extends JpaRepository<KnowledgeVersion, UUID> {

    Page<KnowledgeVersion> findByArticleArticleIdOrderByVersionNumberDesc(UUID articleId, Pageable pageable);

    Optional<KnowledgeVersion> findByArticleArticleIdAndVersionNumber(UUID articleId, Integer versionNumber);

    @Query("SELECT MAX(v.versionNumber) FROM KnowledgeVersion v WHERE v.article.articleId = :articleId")
    Optional<Integer> findMaxVersionNumberByArticleId(@Param("articleId") UUID articleId);

    @Query("SELECT v FROM KnowledgeVersion v WHERE v.article.articleId = :articleId ORDER BY v.versionNumber DESC LIMIT 1")
    Optional<KnowledgeVersion> findLatestVersionByArticleId(@Param("articleId") UUID articleId);
}
