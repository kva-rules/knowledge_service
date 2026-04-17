package com.cognizant.knowledge_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RatingResponseDTO {

    private UUID ratingId;
    private UUID articleId;
    private UUID userId;
    private Integer rating;
    private String feedback;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
