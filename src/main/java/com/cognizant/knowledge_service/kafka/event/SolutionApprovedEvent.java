package com.cognizant.knowledge_service.kafka.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolutionApprovedEvent {

    private UUID solutionId;
    private UUID ticketId;
    private String title;
    private String content;
    private UUID approvedBy;
    private LocalDateTime approvedAt;
}
