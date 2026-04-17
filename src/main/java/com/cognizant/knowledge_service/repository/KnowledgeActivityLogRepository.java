package com.cognizant.knowledge_service.repository;

import com.cognizant.knowledge_service.entity.KnowledgeActivityLog;
import com.cognizant.knowledge_service.enums.ActivityAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KnowledgeActivityLogRepository extends JpaRepository<KnowledgeActivityLog, UUID> {

    Page<KnowledgeActivityLog> findByArticleArticleIdOrderByCreatedAtDesc(UUID articleId, Pageable pageable);

    Page<KnowledgeActivityLog> findByPerformedByOrderByCreatedAtDesc(UUID performedBy, Pageable pageable);

    Page<KnowledgeActivityLog> findByActionOrderByCreatedAtDesc(ActivityAction action, Pageable pageable);
}
