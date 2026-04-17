package com.cognizant.knowledge_service.controller;

import com.cognizant.knowledge_service.dto.request.ArticleRequestDTO;
import com.cognizant.knowledge_service.dto.response.ApiResponseDTO;
import com.cognizant.knowledge_service.dto.response.ArticleResponseDTO;
import com.cognizant.knowledge_service.dto.response.PageResponseDTO;
import com.cognizant.knowledge_service.enums.ArticleStatus;
import com.cognizant.knowledge_service.service.KnowledgeService;
import com.cognizant.knowledge_service.service.ViewService;
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
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final ViewService viewService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseDTO<ArticleResponseDTO>> createArticle(
            @Valid @RequestBody ArticleRequestDTO request,
            @RequestHeader("X-User-Id") UUID userId) {
        ArticleResponseDTO article = knowledgeService.createArticle(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Article created successfully", article));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ArticleResponseDTO>> getArticleById(@PathVariable UUID id) {
        ArticleResponseDTO article = knowledgeService.getArticleById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(article));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<ArticleResponseDTO>>> getAllArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponseDTO<ArticleResponseDTO> articles = knowledgeService.getAllArticles(pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(articles));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<ArticleResponseDTO>>> searchArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) ArticleStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDTO<ArticleResponseDTO> articles = knowledgeService.searchArticles(keyword, categoryId, tag, status, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(articles));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseDTO<ArticleResponseDTO>> updateArticle(
            @PathVariable UUID id,
            @Valid @RequestBody ArticleRequestDTO request,
            @RequestHeader("X-User-Id") UUID userId) {
        ArticleResponseDTO article = knowledgeService.updateArticle(id, request, userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Article updated successfully", article));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteArticle(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        knowledgeService.deleteArticle(id, userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Article deleted successfully", null));
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseDTO<ArticleResponseDTO>> publishArticle(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        ArticleResponseDTO article = knowledgeService.publishArticle(id, userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Article published successfully", article));
    }

    @PutMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseDTO<ArticleResponseDTO>> archiveArticle(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        ArticleResponseDTO article = knowledgeService.archiveArticle(id, userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Article archived successfully", article));
    }

    @PostMapping("/{articleId}/view")
    public ResponseEntity<ApiResponseDTO<Void>> trackView(
            @PathVariable UUID articleId,
            @RequestHeader("X-User-Id") UUID userId) {
        viewService.trackView(articleId, userId);
        return ResponseEntity.ok(ApiResponseDTO.success("View tracked successfully", null));
    }
}
