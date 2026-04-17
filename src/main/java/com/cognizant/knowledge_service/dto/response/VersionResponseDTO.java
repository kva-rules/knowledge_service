package com.cognizant.knowledge_service.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VersionResponseDTO {

    private UUID versionId;
    private UUID articleId;
    private Integer versionNumber;
    private String title;
    private String content;
    private UUID editedBy;
    private LocalDateTime createdAt;
}
