package com.cognizant.knowledge_service.kafka.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResolvedEvent {

    private UUID ticketId;
    private String title;
    private String description;
    private String resolution;
    private UUID resolvedBy;
    private LocalDateTime resolvedAt;
}
