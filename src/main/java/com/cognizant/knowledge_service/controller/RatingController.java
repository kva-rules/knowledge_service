package com.cognizant.knowledge_service.controller;

import com.cognizant.knowledge_service.dto.request.RatingRequestDTO;
import com.cognizant.knowledge_service.dto.response.ApiResponseDTO;
import com.cognizant.knowledge_service.dto.response.PageResponseDTO;
import com.cognizant.knowledge_service.dto.response.RatingResponseDTO;
import com.cognizant.knowledge_service.service.RatingService;
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
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<ApiResponseDTO<RatingResponseDTO>> createOrUpdateRating(
            @Valid @RequestBody RatingRequestDTO request,
            @RequestHeader("X-User-Id") UUID userId) {
        RatingResponseDTO rating = ratingService.createOrUpdateRating(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Rating submitted successfully", rating));
    }

    @GetMapping("/article/{articleId}")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<RatingResponseDTO>>> getRatingsByArticleId(
            @PathVariable UUID articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PageResponseDTO<RatingResponseDTO> ratings = ratingService.getRatingsByArticleId(articleId, pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(ratings));
    }

    @GetMapping("/article/{articleId}/average")
    public ResponseEntity<ApiResponseDTO<Double>> getAverageRating(@PathVariable UUID articleId) {
        Double averageRating = ratingService.getAverageRating(articleId);
        return ResponseEntity.ok(ApiResponseDTO.success(averageRating));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteRating(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") UUID userId) {
        ratingService.deleteRating(id, userId);
        return ResponseEntity.ok(ApiResponseDTO.success("Rating deleted successfully", null));
    }
}
