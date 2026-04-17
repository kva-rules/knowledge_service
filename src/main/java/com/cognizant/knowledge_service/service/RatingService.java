package com.cognizant.knowledge_service.service;

import com.cognizant.knowledge_service.dto.request.RatingRequestDTO;
import com.cognizant.knowledge_service.dto.response.PageResponseDTO;
import com.cognizant.knowledge_service.dto.response.RatingResponseDTO;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RatingService {

    RatingResponseDTO createOrUpdateRating(RatingRequestDTO request, UUID userId);

    PageResponseDTO<RatingResponseDTO> getRatingsByArticleId(UUID articleId, Pageable pageable);

    Double getAverageRating(UUID articleId);

    void deleteRating(UUID ratingId, UUID userId);
}
