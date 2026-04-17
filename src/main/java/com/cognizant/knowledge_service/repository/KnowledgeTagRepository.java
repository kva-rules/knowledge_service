package com.cognizant.knowledge_service.repository;

import com.cognizant.knowledge_service.entity.KnowledgeTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface KnowledgeTagRepository extends JpaRepository<KnowledgeTag, UUID> {

    Optional<KnowledgeTag> findByTagName(String tagName);

    boolean existsByTagName(String tagName);
}
