package com.cognizant.knowledge_service.controller;

import com.cognizant.knowledge_service.dto.request.ArticleRequestDTO;
import com.cognizant.knowledge_service.dto.response.ApiResponseDTO;
import com.cognizant.knowledge_service.dto.response.ArticleResponseDTO;
import com.cognizant.knowledge_service.dto.response.PageResponseDTO;
import com.cognizant.knowledge_service.enums.ArticleStatus;
import com.cognizant.knowledge_service.service.KnowledgeService;
import com.cognizant.knowledge_service.service.ViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
@Tag(name = "Knowledge Articles", description = "CRUD + search + view-tracking for KB articles")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final ViewService viewService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new knowledge article", description = "Creates a KB article authored by the caller")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Article created"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<ArticleResponseDTO>> createArticle(
            @Valid @RequestBody ArticleRequestDTO request,
            @Parameter(description = "Authenticated user ID from gateway") @RequestHeader("X-User-Id") UUID userId) {
        ArticleResponseDTO article = knowledgeService.createArticle(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Article created successfully", article));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get article by ID", description = "Fetches a single KB article by its UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Article found"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<ArticleResponseDTO>> getArticleById(
            @Parameter(description = "Article UUID") @PathVariable UUID id) {
        ArticleResponseDTO article = knowledgeService.getArticleById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(article));
    }

    @GetMapping
    @Operation(summary = "List all articles", description = "Returns a paginated list of KB articles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<ArticleResponseDTO>>> getAllArticles(
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction: asc or desc") @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponseDTO<ArticleResponseDTO> articles = knowledgeService.getAllArticles(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(articles));
    }

    @GetMapping("/search")
    @Operation(summary = "Search articles", description = "Filter articles by keyword, category, tag, or status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Results returned"),
            @ApiResponse(responseCode = "400", description = "Invalid filter values")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<ArticleResponseDTO>>> searchArticles(
            @Parameter(description = "Free-text keyword") @RequestParam(required = false) String keyword,
            @Parameter(description = "Category UUID filter") @RequestParam(required = false) UUID categoryId,
            @Parameter(description = "Tag name filter") @RequestParam(required = false) String tag,
            @Parameter(description = "Article status filter") @RequestParam(required = false) ArticleStatus status,
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDTO<ArticleResponseDTO> articles = knowledgeService.searchArticles(keyword, categoryId, tag, status, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(articles));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update an article", description = "Updates title, content, or metadata of an existing article")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Article updated"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<ArticleResponseDTO>> updateArticle(
            @Parameter(description = "Article UUID") @PathVariable UUID id,
            @Valid @RequestBody ArticleRequestDTO request,
            @Parameter(description = "Authenticated user ID from gateway") @RequestHeader("X-User-Id") UUID userId) {
        ArticleResponseDTO article = knowledgeService.updateArticle(id, request, userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Article updated successfully", article));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete an article", description = "Soft-deletes a KB article (admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Article deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<Void>> deleteArticle(
            @Parameter(description = "Article UUID") @PathVariable UUID id,
            @Parameter(description = "Authenticated user ID from gateway") @RequestHeader("X-User-Id") UUID userId) {
        knowledgeService.deleteArticle(id, userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Article deleted successfully", null));
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Publish an article", description = "Transitions an article to PUBLISHED status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Article published"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<ArticleResponseDTO>> publishArticle(
            @Parameter(description = "Article UUID") @PathVariable UUID id,
            @Parameter(description = "Authenticated user ID from gateway") @RequestHeader("X-User-Id") UUID userId) {
        ArticleResponseDTO article = knowledgeService.publishArticle(id, userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Article published successfully", article));
    }

    @PutMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Archive an article", description = "Transitions an article to ARCHIVED status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Article archived"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<ArticleResponseDTO>> archiveArticle(
            @Parameter(description = "Article UUID") @PathVariable UUID id,
            @Parameter(description = "Authenticated user ID from gateway") @RequestHeader("X-User-Id") UUID userId) {
        ArticleResponseDTO article = knowledgeService.archiveArticle(id, userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Article archived successfully", article));
    }

    @PostMapping("/{articleId}/view")
    @Operation(summary = "Track an article view", description = "Records a view event for analytics")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "View tracked"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<Void>> trackView(
            @Parameter(description = "Article UUID") @PathVariable UUID articleId,
            @Parameter(description = "Authenticated user ID from gateway") @RequestHeader("X-User-Id") UUID userId) {
        viewService.trackView(articleId, userId);
        return ResponseEntity.ok(ApiResponseDTO.success("View tracked successfully", null));
    }
}
