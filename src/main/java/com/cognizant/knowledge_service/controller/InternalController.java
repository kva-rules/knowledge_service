package com.cognizant.knowledge_service.controller;

import com.cognizant.knowledge_service.dto.response.ApiResponseDTO;
import com.cognizant.knowledge_service.dto.response.ArticleResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeArticle;
import com.cognizant.knowledge_service.mapper.ArticleMapper;
import com.cognizant.knowledge_service.repository.KnowledgeArticleRepository;
import com.cognizant.knowledge_service.repository.KnowledgeRatingRepository;
import com.cognizant.knowledge_service.repository.KnowledgeViewRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@Tag(name = "Knowledge (Internal)", description = "Service-to-service article lookups")
public class InternalController {

    private final KnowledgeArticleRepository articleRepository;
    private final KnowledgeRatingRepository ratingRepository;
    private final KnowledgeViewRepository viewRepository;
    private final ArticleMapper articleMapper;

    @GetMapping("/tickets/{ticketId}/knowledge")
    @Operation(summary = "Get articles linked to a ticket", description = "Returns KB articles associated with a ticket")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Articles returned"),
            @ApiResponse(responseCode = "404", description = "Ticket not found")
    })
    public ResponseEntity<ApiResponseDTO<List<ArticleResponseDTO>>> getArticlesByTicketId(
            @Parameter(description = "Ticket UUID") @PathVariable UUID ticketId) {
        List<KnowledgeArticle> articles = articleRepository.findByTicketIdAndDeletedFalse(ticketId);
        List<ArticleResponseDTO> response = articles.stream()
                .map(article -> enrichArticleResponse(articleMapper.toResponseDTO(article), article.getArticleId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponseDTO.success(response));
    }

    @GetMapping("/solutions/{solutionId}/knowledge")
    @Operation(summary = "Get articles linked to a solution", description = "Returns KB articles associated with a solution")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Articles returned"),
            @ApiResponse(responseCode = "404", description = "Solution not found")
    })
    public ResponseEntity<ApiResponseDTO<List<ArticleResponseDTO>>> getArticlesBySolutionId(
            @Parameter(description = "Solution UUID") @PathVariable UUID solutionId) {
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
