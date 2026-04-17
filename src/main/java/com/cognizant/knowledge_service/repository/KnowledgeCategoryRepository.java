package com.cognizant.knowledge_service.repository;

import com.cognizant.knowledge_service.entity.KnowledgeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface KnowledgeCategoryRepository extends JpaRepository<KnowledgeCategory, UUID> {

    Optional<KnowledgeCategory> findByCategoryName(String categoryName);

    List<KnowledgeCategory> findByParentId(UUID parentId);

    List<KnowledgeCategory> findByParentIdIsNull();

    boolean existsByCategoryName(String categoryName);
}
