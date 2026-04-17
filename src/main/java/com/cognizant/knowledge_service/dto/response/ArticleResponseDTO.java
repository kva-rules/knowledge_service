package com.cognizant.knowledge_service.dto.response;

import com.cognizant.knowledge_service.enums.ArticleStatus;
import com.cognizant.knowledge_service.enums.Visibility;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleResponseDTO {

    private UUID articleId;
    private String title;
    private String content;
    private CategoryResponseDTO category;
    private Set<TagResponseDTO> tags;
    private Double averageRating;
    private Long totalViews;
    private Integer version;
    private ArticleStatus status;
    private Visibility visibility;
    private UUID createdBy;
    private UUID ticketId;
    private UUID solutionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
