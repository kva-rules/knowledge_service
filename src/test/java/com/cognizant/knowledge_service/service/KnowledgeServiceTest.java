package com.cognizant.knowledge_service.service;

import com.cognizant.knowledge_service.dto.request.ArticleRequestDTO;
import com.cognizant.knowledge_service.dto.response.ArticleResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeArticle;
import com.cognizant.knowledge_service.entity.KnowledgeCategory;
import com.cognizant.knowledge_service.enums.ArticleStatus;
import com.cognizant.knowledge_service.enums.Visibility;
import com.cognizant.knowledge_service.exception.ResourceNotFoundException;
import com.cognizant.knowledge_service.kafka.KnowledgeEventProducer;
import com.cognizant.knowledge_service.mapper.ArticleMapper;
import com.cognizant.knowledge_service.repository.*;
import com.cognizant.knowledge_service.service.impl.KnowledgeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KnowledgeServiceTest {

    @Mock
    private KnowledgeArticleRepository articleRepository;

    @Mock
    private KnowledgeVersionRepository versionRepository;

    @Mock
    private KnowledgeCategoryRepository categoryRepository;

    @Mock
    private KnowledgeTagRepository tagRepository;

    @Mock
    private KnowledgeRatingRepository ratingRepository;

    @Mock
    private KnowledgeViewRepository viewRepository;

    @Mock
    private ArticleMapper articleMapper;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private KnowledgeEventProducer eventProducer;

    @InjectMocks
    private KnowledgeServiceImpl knowledgeService;

    private UUID userId;
    private UUID articleId;
    private UUID categoryId;
    private KnowledgeArticle article;
    private KnowledgeCategory category;
    private ArticleRequestDTO articleRequest;
    private ArticleResponseDTO articleResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        articleId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        category = KnowledgeCategory.builder()
                .categoryId(categoryId)
                .categoryName("Test Category")
                .description("Test Description")
                .build();

        article = KnowledgeArticle.builder()
                .articleId(articleId)
                .title("Test Article")
                .content("Test Content")
                .category(category)
                .createdBy(userId)
                .status(ArticleStatus.DRAFT)
                .visibility(Visibility.ORGANIZATION)
                .version(1)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        articleRequest = ArticleRequestDTO.builder()
                .title("Test Article")
                .content("Test Content")
                .categoryId(categoryId)
                .tags(Set.of("tag1", "tag2"))
                .visibility(Visibility.ORGANIZATION)
                .build();

        articleResponse = ArticleResponseDTO.builder()
                .articleId(articleId)
                .title("Test Article")
                .content("Test Content")
                .status(ArticleStatus.DRAFT)
                .visibility(Visibility.ORGANIZATION)
                .version(1)
                .build();
    }

    @Test
    @DisplayName("Should create article successfully")
    void createArticle_Success() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(tagRepository.findByTagName(anyString())).thenReturn(Optional.empty());
        when(tagRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(articleRepository.save(any(KnowledgeArticle.class))).thenReturn(article);
        when(versionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(articleMapper.toResponseDTO(any(KnowledgeArticle.class))).thenReturn(articleResponse);
        when(ratingRepository.findAverageRatingByArticleId(any())).thenReturn(Optional.of(4.5));
        when(viewRepository.countByArticleId(any())).thenReturn(100L);

        ArticleResponseDTO result = knowledgeService.createArticle(articleRequest, userId);

        assertNotNull(result);
        assertEquals("Test Article", result.getTitle());
        verify(articleRepository).save(any(KnowledgeArticle.class));
        verify(versionRepository).save(any());
        verify(activityLogService).logActivity(any(), any(), any(), any());
        verify(eventProducer).sendKnowledgeCreatedEvent(any(), any(), any());
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void createArticle_CategoryNotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                knowledgeService.createArticle(articleRequest, userId));

        verify(articleRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get article by ID successfully")
    void getArticleById_Success() {
        when(articleRepository.findByArticleIdAndDeletedFalse(articleId)).thenReturn(Optional.of(article));
        when(articleMapper.toResponseDTO(article)).thenReturn(articleResponse);
        when(ratingRepository.findAverageRatingByArticleId(articleId)).thenReturn(Optional.of(4.5));
        when(viewRepository.countByArticleId(articleId)).thenReturn(100L);

        ArticleResponseDTO result = knowledgeService.getArticleById(articleId);

        assertNotNull(result);
        assertEquals(articleId, result.getArticleId());
    }

    @Test
    @DisplayName("Should throw exception when article not found")
    void getArticleById_NotFound() {
        when(articleRepository.findByArticleIdAndDeletedFalse(articleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                knowledgeService.getArticleById(articleId));
    }

    @Test
    @DisplayName("Should update article successfully")
    void updateArticle_Success() {
        article.setStatus(ArticleStatus.DRAFT);
        when(articleRepository.findByArticleIdAndDeletedFalse(articleId)).thenReturn(Optional.of(article));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(tagRepository.findByTagName(anyString())).thenReturn(Optional.empty());
        when(tagRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(articleRepository.save(any(KnowledgeArticle.class))).thenReturn(article);
        when(versionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(articleMapper.toResponseDTO(any(KnowledgeArticle.class))).thenReturn(articleResponse);
        when(ratingRepository.findAverageRatingByArticleId(any())).thenReturn(Optional.of(4.5));
        when(viewRepository.countByArticleId(any())).thenReturn(100L);

        ArticleResponseDTO result = knowledgeService.updateArticle(articleId, articleRequest, userId);

        assertNotNull(result);
        verify(articleRepository).save(any(KnowledgeArticle.class));
        verify(versionRepository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating published article")
    void updateArticle_PublishedArticle() {
        article.setStatus(ArticleStatus.PUBLISHED);
        when(articleRepository.findByArticleIdAndDeletedFalse(articleId)).thenReturn(Optional.of(article));

        assertThrows(IllegalStateException.class, () ->
                knowledgeService.updateArticle(articleId, articleRequest, userId));
    }

    @Test
    @DisplayName("Should publish article successfully")
    void publishArticle_Success() {
        article.setStatus(ArticleStatus.DRAFT);
        KnowledgeArticle publishedArticle = KnowledgeArticle.builder()
                .articleId(articleId)
                .title("Test Article")
                .content("Test Content")
                .status(ArticleStatus.PUBLISHED)
                .build();

        ArticleResponseDTO publishedResponse = ArticleResponseDTO.builder()
                .articleId(articleId)
                .status(ArticleStatus.PUBLISHED)
                .build();

        when(articleRepository.findByArticleIdAndDeletedFalse(articleId)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(KnowledgeArticle.class))).thenReturn(publishedArticle);
        when(articleMapper.toResponseDTO(any(KnowledgeArticle.class))).thenReturn(publishedResponse);
        when(ratingRepository.findAverageRatingByArticleId(any())).thenReturn(Optional.of(4.5));
        when(viewRepository.countByArticleId(any())).thenReturn(100L);

        ArticleResponseDTO result = knowledgeService.publishArticle(articleId, userId);

        assertNotNull(result);
        assertEquals(ArticleStatus.PUBLISHED, result.getStatus());
        verify(eventProducer).sendKnowledgePublishedEvent(any(), any(), any());
    }

    @Test
    @DisplayName("Should throw exception when publishing already published article")
    void publishArticle_AlreadyPublished() {
        article.setStatus(ArticleStatus.PUBLISHED);
        when(articleRepository.findByArticleIdAndDeletedFalse(articleId)).thenReturn(Optional.of(article));

        assertThrows(IllegalStateException.class, () ->
                knowledgeService.publishArticle(articleId, userId));
    }

    @Test
    @DisplayName("Should archive article successfully")
    void archiveArticle_Success() {
        ArticleResponseDTO archivedResponse = ArticleResponseDTO.builder()
                .articleId(articleId)
                .status(ArticleStatus.ARCHIVED)
                .build();

        when(articleRepository.findByArticleIdAndDeletedFalse(articleId)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(KnowledgeArticle.class))).thenReturn(article);
        when(articleMapper.toResponseDTO(any(KnowledgeArticle.class))).thenReturn(archivedResponse);
        when(ratingRepository.findAverageRatingByArticleId(any())).thenReturn(Optional.of(4.5));
        when(viewRepository.countByArticleId(any())).thenReturn(100L);

        ArticleResponseDTO result = knowledgeService.archiveArticle(articleId, userId);

        assertNotNull(result);
        verify(articleRepository).save(any(KnowledgeArticle.class));
    }

    @Test
    @DisplayName("Should soft delete article successfully")
    void deleteArticle_Success() {
        when(articleRepository.findByArticleIdAndDeletedFalse(articleId)).thenReturn(Optional.of(article));
        when(articleRepository.save(any(KnowledgeArticle.class))).thenReturn(article);

        knowledgeService.deleteArticle(articleId, userId);

        verify(articleRepository).save(argThat(a -> a.getDeleted()));
    }
}
