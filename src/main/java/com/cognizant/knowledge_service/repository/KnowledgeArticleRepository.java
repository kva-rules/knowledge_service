package com.cognizant.knowledge_service.repository;

import com.cognizant.knowledge_service.domain.KnowledgeArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface KnowledgeArticleRepository extends JpaRepository<KnowledgeArticle, UUID> {

    KnowledgeArticle findByName(String name);
}



