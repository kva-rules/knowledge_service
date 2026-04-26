package com.cognizant.knowledge_service.controller;

import com.cognizant.knowledge_service.dto.request.RatingRequestDTO;
import com.cognizant.knowledge_service.dto.response.ApiResponseDTO;
import com.cognizant.knowledge_service.dto.response.PageResponseDTO;
import com.cognizant.knowledge_service.dto.response.RatingResponseDTO;
import com.cognizant.knowledge_service.service.RatingService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
@Tag(name = "Knowledge Ratings", description = "User ratings on articles")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    @Operation(summary = "Create or update a rating", description = "Upserts the caller's rating for an article")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rating submitted"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<RatingResponseDTO>> createOrUpdateRating(
            @Valid @RequestBody RatingRequestDTO request,
            @Parameter(description = "Authenticated user ID from gateway") @RequestHeader("X-User-Id") UUID userId) {
        RatingResponseDTO rating = ratingService.createOrUpdateRating(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Rating submitted successfully", rating));
    }

    @GetMapping("/article/{articleId}")
    @Operation(summary = "List ratings for an article", description = "Returns a paginated list of article ratings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ratings returned"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<RatingResponseDTO>>> getRatingsByArticleId(
            @Parameter(description = "Article UUID") @PathVariable UUID articleId,
            @Parameter(description = "Zero-based page index") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDTO<RatingResponseDTO> ratings = ratingService.getRatingsByArticleId(articleId, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(ratings));
    }

    @GetMapping("/article/{articleId}/average")
    @Operation(summary = "Get average rating", description = "Returns the mean rating score for an article")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Average returned"),
            @ApiResponse(responseCode = "404", description = "Article not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<Double>> getAverageRating(
            @Parameter(description = "Article UUID") @PathVariable UUID articleId) {
        Double averageRating = ratingService.getAverageRating(articleId);
        return ResponseEntity.ok(ApiResponseDTO.success(averageRating));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a rating", description = "Removes the caller's rating by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Rating deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Rating not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<Void>> deleteRating(
            @Parameter(description = "Rating UUID") @PathVariable UUID id,
            @Parameter(description = "Authenticated user ID from gateway") @RequestHeader("X-User-Id") UUID userId) {
        ratingService.deleteRating(id, userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Rating deleted successfully", null));
    }
}
