package com.cognizant.knowledge_service.service.impl;

import com.cognizant.knowledge_service.dto.request.ArticleRequestDTO;
import com.cognizant.knowledge_service.dto.response.ArticleResponseDTO;
import com.cognizant.knowledge_service.dto.response.PageResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeArticle;
import com.cognizant.knowledge_service.entity.KnowledgeCategory;
import com.cognizant.knowledge_service.entity.KnowledgeTag;
import com.cognizant.knowledge_service.entity.KnowledgeVersion;
import com.cognizant.knowledge_service.enums.ActivityAction;
import com.cognizant.knowledge_service.enums.ArticleStatus;
import com.cognizant.knowledge_service.enums.Visibility;
import com.cognizant.knowledge_service.exception.ResourceNotFoundException;
import com.cognizant.knowledge_service.kafka.KnowledgeEventProducer;
import com.cognizant.knowledge_service.mapper.ArticleMapper;
import com.cognizant.knowledge_service.repository.*;
import com.cognizant.knowledge_service.service.ActivityLogService;
import com.cognizant.knowledge_service.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeServiceImpl implements KnowledgeService {

    private final KnowledgeArticleRepository articleRepository;
    private final KnowledgeVersionRepository versionRepository;
    private final KnowledgeCategoryRepository categoryRepository;
    private final KnowledgeTagRepository tagRepository;
    private final KnowledgeRatingRepository ratingRepository;
    private final KnowledgeViewRepository viewRepository;
    private final ArticleMapper articleMapper;
    private final ActivityLogService activityLogService;
    private final KnowledgeEventProducer eventProducer;

    @Override
    @Transactional
    public ArticleResponseDTO createArticle(ArticleRequestDTO request, UUID createdBy) {
        KnowledgeCategory category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        }

        Set<KnowledgeTag> tags = processTags(request.getTags());

        KnowledgeArticle article = KnowledgeArticle.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(category)
                .tags(tags)
                .createdBy(createdBy)
                .ticketId(request.getTicketId())
                .solutionId(request.getSolutionId())
                .status(ArticleStatus.DRAFT)
                .visibility(request.getVisibility() != null ? request.getVisibility() : Visibility.ORGANIZATION)
                .version(1)
                .build();

        article = articleRepository.save(article);

        // Create initial version
        createVersion(article, createdBy);

        activityLogService.logActivity(article, ActivityAction.CREATED, createdBy, "Article created");
        eventProducer.sendKnowledgeCreatedEvent(article.getArticleId(), article.getTitle(), createdBy);

        log.info("Article created: {}", article.getArticleId());
        return enrichArticleResponse(articleMapper.toResponseDTO(article), article.getArticleId());
    }

    @Override
    @Transactional
    public ArticleResponseDTO updateArticle(UUID articleId, ArticleRequestDTO request, UUID updatedBy) {
        KnowledgeArticle article = articleRepository.findByArticleIdAndDeletedFalse(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        if (article.getStatus() == ArticleStatus.PUBLISHED) {
            throw new IllegalStateException("Cannot update a published article. Archive it first.");
        }

        KnowledgeCategory category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        }

        Set<KnowledgeTag> tags = processTags(request.getTags());

        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        article.setCategory(category);
        article.setTags(tags);
        if (request.getVisibility() != null) {
            article.setVisibility(request.getVisibility());
        }

        // Increment version
        article.setVersion(article.getVersion() + 1);
        article = articleRepository.save(article);

        // Create new version record
        createVersion(article, updatedBy);

        activityLogService.logActivity(article, ActivityAction.UPDATED, updatedBy, "Article updated to version " + article.getVersion());
        eventProducer.sendKnowledgeUpdatedEvent(article.getArticleId(), article.getTitle(), updatedBy, article.getVersion());

        log.info("Article updated: {} to version {}", articleId, article.getVersion());
        return enrichArticleResponse(articleMapper.toResponseDTO(article), articleId);
    }

    @Override
    public ArticleResponseDTO getArticleById(UUID articleId) {
        KnowledgeArticle article = articleRepository.findByArticleIdAndDeletedFalse(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));
        return enrichArticleResponse(articleMapper.toResponseDTO(article), articleId);
    }

    @Override
    public PageResponseDTO<ArticleResponseDTO> getAllArticles(Pageable pageable) {
        Page<KnowledgeArticle> articlesPage = articleRepository.findByDeletedFalse(pageable);
        return buildPageResponse(articlesPage);
    }

    @Override
    public PageResponseDTO<ArticleResponseDTO> searchArticles(String keyword, UUID categoryId, String tag, ArticleStatus status, Pageable pageable) {
        Page<KnowledgeArticle> articlesPage = articleRepository.searchArticles(keyword, categoryId, tag, status, pageable);
        return buildPageResponse(articlesPage);
    }

    @Override
    @Transactional
    public ArticleResponseDTO publishArticle(UUID articleId, UUID publishedBy) {
        KnowledgeArticle article = articleRepository.findByArticleIdAndDeletedFalse(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        if (article.getStatus() == ArticleStatus.PUBLISHED) {
            throw new IllegalStateException("Article is already published");
        }

        article.setStatus(ArticleStatus.PUBLISHED);
        article = articleRepository.save(article);

        activityLogService.logActivity(article, ActivityAction.PUBLISHED, publishedBy, "Article published");
        eventProducer.sendKnowledgePublishedEvent(article.getArticleId(), article.getTitle(), publishedBy);

        log.info("Article published: {}", articleId);
        return enrichArticleResponse(articleMapper.toResponseDTO(article), articleId);
    }

    @Override
    @Transactional
    public ArticleResponseDTO archiveArticle(UUID articleId, UUID archivedBy) {
        KnowledgeArticle article = articleRepository.findByArticleIdAndDeletedFalse(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        article.setStatus(ArticleStatus.ARCHIVED);
        article = articleRepository.save(article);

        activityLogService.logActivity(article, ActivityAction.ARCHIVED, archivedBy, "Article archived");

        log.info("Article archived: {}", articleId);
        return enrichArticleResponse(articleMapper.toResponseDTO(article), articleId);
    }

    @Override
    @Transactional
    public void deleteArticle(UUID articleId, UUID deletedBy) {
        KnowledgeArticle article = articleRepository.findByArticleIdAndDeletedFalse(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        article.setDeleted(true);
        articleRepository.save(article);

        log.info("Article soft-deleted: {}", articleId);
    }

    @Override
    @Transactional
    public ArticleResponseDTO createArticleFromSolution(UUID solutionId, String title, String content, UUID createdBy) {
        ArticleRequestDTO request = ArticleRequestDTO.builder()
                .title(title)
                .content(content)
                .solutionId(solutionId)
                .visibility(Visibility.ORGANIZATION)
                .build();

        ArticleResponseDTO article = createArticle(request, createdBy);
        log.info("Article created from solution: {} -> {}", solutionId, article.getArticleId());
        return article;
    }

    private Set<KnowledgeTag> processTags(Set<String> tagNames) {
        Set<KnowledgeTag> tags = new HashSet<>();
        if (tagNames != null && !tagNames.isEmpty()) {
            for (String tagName : tagNames) {
                KnowledgeTag tag = tagRepository.findByTagName(tagName)
                        .orElseGet(() -> tagRepository.save(KnowledgeTag.builder().tagName(tagName).build()));
                tags.add(tag);
            }
        }
        return tags;
    }

    private void createVersion(KnowledgeArticle article, UUID editedBy) {
        KnowledgeVersion version = KnowledgeVersion.builder()
                .article(article)
                .versionNumber(article.getVersion())
                .title(article.getTitle())
                .content(article.getContent())
                .editedBy(editedBy)
                .build();
        versionRepository.save(version);
    }

    private ArticleResponseDTO enrichArticleResponse(ArticleResponseDTO dto, UUID articleId) {
        dto.setAverageRating(ratingRepository.findAverageRatingByArticleId(articleId).orElse(0.0));
        dto.setTotalViews(viewRepository.countByArticleId(articleId));
        return dto;
    }

    private PageResponseDTO<ArticleResponseDTO> buildPageResponse(Page<KnowledgeArticle> page) {
        return PageResponseDTO.<ArticleResponseDTO>builder()
                .content(page.getContent().stream()
                        .map(article -> enrichArticleResponse(articleMapper.toResponseDTO(article), article.getArticleId()))
                        .toList())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }
}
