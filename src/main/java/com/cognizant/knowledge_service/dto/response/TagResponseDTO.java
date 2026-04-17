package com.cognizant.knowledge_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponseDTO {

    private UUID tagId;
    private String tagName;
    private LocalDateTime createdAt;
}
