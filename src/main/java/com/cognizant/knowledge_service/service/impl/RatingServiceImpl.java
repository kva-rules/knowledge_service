package com.cognizant.knowledge_service.service.impl;

import com.cognizant.knowledge_service.dto.request.RatingRequestDTO;
import com.cognizant.knowledge_service.dto.response.PageResponseDTO;
import com.cognizant.knowledge_service.dto.response.RatingResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeArticle;
import com.cognizant.knowledge_service.entity.KnowledgeRating;
import com.cognizant.knowledge_service.enums.ActivityAction;
import com.cognizant.knowledge_service.exception.ResourceNotFoundException;
import com.cognizant.knowledge_service.kafka.KnowledgeEventProducer;
import com.cognizant.knowledge_service.mapper.RatingMapper;
import com.cognizant.knowledge_service.repository.KnowledgeArticleRepository;
import com.cognizant.knowledge_service.repository.KnowledgeRatingRepository;
import com.cognizant.knowledge_service.service.ActivityLogService;
import com.cognizant.knowledge_service.service.RatingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final KnowledgeRatingRepository ratingRepository;
    private final KnowledgeArticleRepository articleRepository;
    private final RatingMapper ratingMapper;
    private final ActivityLogService activityLogService;
    private final KnowledgeEventProducer eventProducer;

    @Override
    @Transactional
    public RatingResponseDTO createOrUpdateRating(RatingRequestDTO request, UUID userId) {
        KnowledgeArticle article = articleRepository.findByArticleIdAndDeletedFalse(request.getArticleId())
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + request.getArticleId()));

        Optional<KnowledgeRating> existingRating = ratingRepository
                .findByArticleArticleIdAndUserId(request.getArticleId(), userId);

        KnowledgeRating rating;
        if (existingRating.isPresent()) {
            rating = existingRating.get();
            rating.setRating(request.getRating());
            rating.setFeedback(request.getFeedback());
            log.info("Rating updated for article {} by user {}", request.getArticleId(), userId);
        } else {
            rating = KnowledgeRating.builder()
                    .article(article)
                    .userId(userId)
                    .rating(request.getRating())
                    .feedback(request.getFeedback())
                    .build();
            log.info("Rating created for article {} by user {}", request.getArticleId(), userId);
        }

        rating = ratingRepository.save(rating);
        activityLogService.logActivity(article, ActivityAction.RATED, userId, "Rating: " + request.getRating());
        eventProducer.sendKnowledgeRatedEvent(article.getArticleId(), userId, request.getRating());

        return ratingMapper.toResponseDTO(rating);
    }

    @Override
    public PageResponseDTO<RatingResponseDTO> getRatingsByArticleId(UUID articleId, Pageable pageable) {
        Page<KnowledgeRating> ratingsPage = ratingRepository.findByArticleArticleId(articleId, pageable);

        return PageResponseDTO.<RatingResponseDTO>builder()
                .content(ratingsPage.getContent().stream()
                        .map(ratingMapper::toResponseDTO)
                        .toList())
                .pageNumber(ratingsPage.getNumber())
                .pageSize(ratingsPage.getSize())
                .totalElements(ratingsPage.getTotalElements())
                .totalPages(ratingsPage.getTotalPages())
                .last(ratingsPage.isLast())
                .first(ratingsPage.isFirst())
                .build();
    }

    @Override
    public Double getAverageRating(UUID articleId) {
        return ratingRepository.findAverageRatingByArticleId(articleId).orElse(0.0);
    }

    @Override
    @Transactional
    public void deleteRating(UUID ratingId, UUID userId) {
        KnowledgeRating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found with id: " + ratingId));

        if (!rating.getUserId().equals(userId)) {
            throw new IllegalStateException("User can only delete their own ratings");
        }

        ratingRepository.delete(rating);
        log.info("Rating deleted: {}", ratingId);
    }
}
