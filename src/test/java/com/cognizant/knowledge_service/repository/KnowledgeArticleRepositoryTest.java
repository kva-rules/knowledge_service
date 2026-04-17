package com.cognizant.knowledge_service.repository;

import com.cognizant.knowledge_service.entity.KnowledgeArticle;
import com.cognizant.knowledge_service.entity.KnowledgeCategory;
import com.cognizant.knowledge_service.enums.ArticleStatus;
import com.cognizant.knowledge_service.enums.Visibility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class KnowledgeArticleRepositoryTest {

    @Autowired
    private KnowledgeArticleRepository articleRepository;

    @Autowired
    private KnowledgeCategoryRepository categoryRepository;

    private KnowledgeCategory category;
    private KnowledgeArticle article;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        category = KnowledgeCategory.builder()
                .categoryName("Test Category")
                .description("Test Description")
                .build();
        category = categoryRepository.save(category);

        article = KnowledgeArticle.builder()
                .title("Test Article")
                .content("Test Content for searching")
                .category(category)
                .createdBy(userId)
                .status(ArticleStatus.PUBLISHED)
                .visibility(Visibility.ORGANIZATION)
                .version(1)
                .deleted(false)
                .build();
        article = articleRepository.save(article);
    }

    @Test
    @DisplayName("Should find article by ID when not deleted")
    void findByArticleIdAndDeletedFalse_Success() {
        Optional<KnowledgeArticle> result = articleRepository.findByArticleIdAndDeletedFalse(article.getArticleId());

        assertTrue(result.isPresent());
        assertEquals(article.getArticleId(), result.get().getArticleId());
    }

    @Test
    @DisplayName("Should not find deleted article")
    void findByArticleIdAndDeletedFalse_DeletedArticle() {
        article.setDeleted(true);
        articleRepository.save(article);

        Optional<KnowledgeArticle> result = articleRepository.findByArticleIdAndDeletedFalse(article.getArticleId());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should find articles by category")
    void findByCategoryCategoryIdAndDeletedFalse_Success() {
        Page<KnowledgeArticle> result = articleRepository.findByCategoryCategoryIdAndDeletedFalse(
                category.getCategoryId(), PageRequest.of(0, 10));

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should find articles by status")
    void findByStatusAndDeletedFalse_Success() {
        Page<KnowledgeArticle> result = articleRepository.findByStatusAndDeletedFalse(
                ArticleStatus.PUBLISHED, PageRequest.of(0, 10));

        assertFalse(result.isEmpty());
        assertEquals(ArticleStatus.PUBLISHED, result.getContent().get(0).getStatus());
    }

    @Test
    @DisplayName("Should search articles by keyword")
    void searchByKeyword_Success() {
        Page<KnowledgeArticle> result = articleRepository.searchByKeyword("Test", PageRequest.of(0, 10));

        assertFalse(result.isEmpty());
    }

    @Test
    @DisplayName("Should find all non-deleted articles")
    void findByDeletedFalse_Success() {
        Page<KnowledgeArticle> result = articleRepository.findByDeletedFalse(PageRequest.of(0, 10));

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Should count articles by category")
    void countByCategoryId_Success() {
        long count = articleRepository.countByCategoryId(category.getCategoryId());

        assertEquals(1, count);
    }
}
