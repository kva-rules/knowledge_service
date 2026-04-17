package com.cognizant.knowledge_service.kafka.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeEvent {

    private String eventType;
    private UUID articleId;
    private String title;
    private UUID performedBy;
    private Integer version;
    private Integer rating;
    private LocalDateTime timestamp;
}
