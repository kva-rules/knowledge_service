package com.cognizant.knowledge_service.kafka;

import com.cognizant.knowledge_service.entity.KnowledgeArticle;
import com.cognizant.knowledge_service.enums.ArticleStatus;
import com.cognizant.knowledge_service.repository.KnowledgeArticleRepository;
import com.library.common.event.SolutionApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SolutionApprovedConsumer {

    private final KnowledgeArticleRepository articleRepository;

    @KafkaListener(topics = "solution.approved", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSolutionApproved(SolutionApprovedEvent event) {
        log.info("Received solution.approved event for solution: {}", event.getSolutionId());
        try {
            KnowledgeArticle article = KnowledgeArticle.builder()
                    .solutionId(event.getSolutionId() != null ? new UUID(0, event.getSolutionId()) : null)
                    .ticketId(event.getTicketId() != null ? new UUID(0, event.getTicketId()) : null)
                    .title("Solution for Ticket #" + event.getTicketId())
                    .content(event.getSolutionText() != null ? event.getSolutionText() : "")
                    .createdBy(event.getContributorIds() != null && !event.getContributorIds().isEmpty() 
                            ? new UUID(0, event.getContributorIds().get(0)) 
                            : UUID.randomUUID())
                    .status(ArticleStatus.PUBLISHED)
                    .version(1)
                    .deleted(false)
                    .build();

            articleRepository.save(article);
            log.info("Created knowledge article from approved solution: {}", article.getArticleId());
        } catch (Exception e) {
            log.error("Error creating knowledge article from solution.approved event: {}", e.getMessage(), e);
        }
    }
}
