package com.cognizant.knowledge_service.service;

import com.cognizant.knowledge_service.dto.request.ArticleRequestDTO;
import com.cognizant.knowledge_service.dto.response.ArticleResponseDTO;
import com.cognizant.knowledge_service.dto.response.PageResponseDTO;
import com.cognizant.knowledge_service.enums.ArticleStatus;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface KnowledgeService {

    ArticleResponseDTO createArticle(ArticleRequestDTO request, UUID createdBy);

    ArticleResponseDTO updateArticle(UUID articleId, ArticleRequestDTO request, UUID updatedBy);

    ArticleResponseDTO getArticleById(UUID articleId);

    PageResponseDTO<ArticleResponseDTO> getAllArticles(Pageable pageable);

    PageResponseDTO<ArticleResponseDTO> searchArticles(String keyword, UUID categoryId, String tag, ArticleStatus status, Pageable pageable);

    ArticleResponseDTO publishArticle(UUID articleId, UUID publishedBy);

    ArticleResponseDTO archiveArticle(UUID articleId, UUID archivedBy);

    void deleteArticle(UUID articleId, UUID deletedBy);

    ArticleResponseDTO createArticleFromSolution(UUID solutionId, String title, String content, UUID createdBy);
}
