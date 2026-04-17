package com.cognizant.knowledge_service.kafka;

import com.cognizant.knowledge_service.kafka.event.SolutionApprovedEvent;
import com.cognizant.knowledge_service.kafka.event.TicketResolvedEvent;
import com.cognizant.knowledge_service.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KnowledgeEventConsumer {

    private final KnowledgeService knowledgeService;

    @KafkaListener(topics = "ticket.resolved", groupId = "knowledge-service-group")
    public void handleTicketResolved(TicketResolvedEvent event) {
        log.info("Received ticket.resolved event for ticket: {}", event.getTicketId());
        try {
            // Log the event - article creation can be triggered manually or via solution approval
            log.info("Ticket {} resolved. Resolution: {}", event.getTicketId(), event.getResolution());
        } catch (Exception e) {
            log.error("Error processing ticket.resolved event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "solution.approved", groupId = "knowledge-service-group")
    public void handleSolutionApproved(SolutionApprovedEvent event) {
        log.info("Received solution.approved event for solution: {}", event.getSolutionId());
        try {
            // Auto-create knowledge article from approved solution
            knowledgeService.createArticleFromSolution(
                    event.getSolutionId(),
                    event.getTitle(),
                    event.getContent(),
                    event.getApprovedBy()
            );
            log.info("Knowledge article created from solution: {}", event.getSolutionId());
        } catch (Exception e) {
            log.error("Error processing solution.approved event: {}", e.getMessage(), e);
        }
    }
}
