package com.cognizant.knowledge_service.service;

import com.cognizant.knowledge_service.dto.request.RatingRequestDTO;
import com.cognizant.knowledge_service.dto.response.RatingResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeArticle;
import com.cognizant.knowledge_service.entity.KnowledgeRating;
import com.cognizant.knowledge_service.exception.ResourceNotFoundException;
import com.cognizant.knowledge_service.kafka.KnowledgeEventProducer;
import com.cognizant.knowledge_service.mapper.RatingMapper;
import com.cognizant.knowledge_service.repository.KnowledgeArticleRepository;
import com.cognizant.knowledge_service.repository.KnowledgeRatingRepository;
import com.cognizant.knowledge_service.service.impl.RatingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private KnowledgeRatingRepository ratingRepository;

    @Mock
    private KnowledgeArticleRepository articleRepository;

    @Mock
    private RatingMapper ratingMapper;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private KnowledgeEventProducer eventProducer;

    @InjectMocks
    private RatingServiceImpl ratingService;

    private UUID userId;
    private UUID articleId;
    private UUID ratingId;
    private KnowledgeArticle article;
    private KnowledgeRating rating;
    private RatingRequestDTO ratingRequest;
    private RatingResponseDTO ratingResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        articleId = UUID.randomUUID();
        ratingId = UUID.randomUUID();

        article = KnowledgeArticle.builder()
                .articleId(articleId)
                .title("Test Article")
                .build();

        rating = KnowledgeRating.builder()
                .ratingId(ratingId)
                .article(article)
                .userId(userId)
                .rating(5)
                .feedback("Great article!")
                .build();

        ratingRequest = RatingRequestDTO.builder()
                .articleId(articleId)
                .rating(5)
                .feedback("Great article!")
                .build();

        ratingResponse = RatingResponseDTO.builder()
                .ratingId(ratingId)
                .articleId(articleId)
                .userId(userId)
                .rating(5)
                .feedback("Great article!")
                .build();
    }

    @Test
    @DisplayName("Should create new rating successfully")
    void createRating_NewRating_Success() {
        when(articleRepository.findByArticleIdAndDeletedFalse(articleId)).thenReturn(Optional.of(article));
        when(ratingRepository.findByArticleArticleIdAndUserId(articleId, userId)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(KnowledgeRating.class))).thenReturn(rating);
        when(ratingMapper.toResponseDTO(any(KnowledgeRating.class))).thenReturn(ratingResponse);

        RatingResponseDTO result = ratingService.createOrUpdateRating(ratingRequest, userId);

        assertNotNull(result);
        assertEquals(5, result.getRating());
        verify(ratingRepository).save(any(KnowledgeRating.class));
        verify(eventProducer).sendKnowledgeRatedEvent(any(), any(), any());
    }

    @Test
    @DisplayName("Should update existing rating successfully")
    void createRating_UpdateExisting_Success() {
        when(articleRepository.findByArticleIdAndDeletedFalse(articleId)).thenReturn(Optional.of(article));
        when(ratingRepository.findByArticleArticleIdAndUserId(articleId, userId)).thenReturn(Optional.of(rating));
        when(ratingRepository.save(any(KnowledgeRating.class))).thenReturn(rating);
        when(ratingMapper.toResponseDTO(any(KnowledgeRating.class))).thenReturn(ratingResponse);

        RatingResponseDTO result = ratingService.createOrUpdateRating(ratingRequest, userId);

        assertNotNull(result);
        verify(ratingRepository).save(any(KnowledgeRating.class));
    }

    @Test
    @DisplayName("Should throw exception when article not found for rating")
    void createRating_ArticleNotFound() {
        when(articleRepository.findByArticleIdAndDeletedFalse(articleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                ratingService.createOrUpdateRating(ratingRequest, userId));
    }

    @Test
    @DisplayName("Should get average rating successfully")
    void getAverageRating_Success() {
        when(ratingRepository.findAverageRatingByArticleId(articleId)).thenReturn(Optional.of(4.5));

        Double result = ratingService.getAverageRating(articleId);

        assertEquals(4.5, result);
    }

    @Test
    @DisplayName("Should return 0 when no ratings exist")
    void getAverageRating_NoRatings() {
        when(ratingRepository.findAverageRatingByArticleId(articleId)).thenReturn(Optional.empty());

        Double result = ratingService.getAverageRating(articleId);

        assertEquals(0.0, result);
    }

    @Test
    @DisplayName("Should delete rating successfully")
    void deleteRating_Success() {
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        ratingService.deleteRating(ratingId, userId);

        verify(ratingRepository).delete(rating);
    }

    @Test
    @DisplayName("Should throw exception when deleting other user's rating")
    void deleteRating_NotOwner() {
        UUID otherUserId = UUID.randomUUID();
        when(ratingRepository.findById(ratingId)).thenReturn(Optional.of(rating));

        assertThrows(IllegalStateException.class, () ->
                ratingService.deleteRating(ratingId, otherUserId));
    }
}
