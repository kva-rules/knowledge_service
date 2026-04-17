package com.cognizant.knowledge_service.controller;

import com.cognizant.knowledge_service.dto.response.ApiResponseDTO;
import com.cognizant.knowledge_service.dto.response.ArticleResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeArticle;
import com.cognizant.knowledge_service.mapper.ArticleMapper;
import com.cognizant.knowledge_service.repository.KnowledgeArticleRepository;
import com.cognizant.knowledge_service.repository.KnowledgeRatingRepository;
import com.cognizant.knowledge_service.repository.KnowledgeViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalController {

    private final KnowledgeArticleRepository articleRepository;
    private final KnowledgeRatingRepository ratingRepository;
    private final KnowledgeViewRepository viewRepository;
    private final ArticleMapper articleMapper;

    @GetMapping("/tickets/{ticketId}/knowledge")
    public ResponseEntity<ApiResponseDTO<List<ArticleResponseDTO>>> getArticlesByTicketId(
            @PathVariable UUID ticketId) {
        List<KnowledgeArticle> articles = articleRepository.findByTicketIdAndDeletedFalse(ticketId);
        List<ArticleResponseDTO> response = articles.stream()
                .map(article -> enrichArticleResponse(articleMapper.toResponseDTO(article), article.getArticleId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponseDTO.success(response));
    }

    @GetMapping("/solutions/{solutionId}/knowledge")
    public ResponseEntity<ApiResponseDTO<List<ArticleResponseDTO>>> getArticlesBySolutionId(
            @PathVariable UUID solutionId) {
        List<KnowledgeArticle> articles = articleRepository.findBySolutionIdAndDeletedFalse(solutionId);
        List<ArticleResponseDTO> response = articles.stream()
                .map(article -> enrichArticleResponse(articleMapper.toResponseDTO(article), article.getArticleId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponseDTO.success(response));
    }

    private ArticleResponseDTO enrichArticleResponse(ArticleResponseDTO dto, UUID articleId) {
        dto.setAverageRating(ratingRepository.findAverageRatingByArticleId(articleId).orElse(0.0));
        dto.setTotalViews(viewRepository.countByArticleId(articleId));
        return dto;
    }
}
