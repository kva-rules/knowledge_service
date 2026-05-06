package com.cognizant.knowledge_service.kafka;

import com.cognizant.knowledge_service.entity.KnowledgeArticle;
import com.cognizant.knowledge_service.enums.ArticleStatus;
import com.cognizant.knowledge_service.enums.Visibility;
import com.cognizant.knowledge_service.repository.KnowledgeArticleRepository;
import com.library.common.event.SolutionApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
            UUID solutionUuid = parseUuid(event.getSolutionId());
            UUID ticketUuid = parseUuid(event.getTicketId());
            UUID createdBy = resolveCreatedBy(event);

            String title = StringUtils.hasText(event.getSolutionTitle())
                    ? event.getSolutionTitle()
                    : "Solution for Ticket " + (ticketUuid != null ? ticketUuid : "Unknown");

            String content = StringUtils.hasText(event.getSolutionText())
                    ? event.getSolutionText()
                    : "";

            // Idempotency check
            if (solutionUuid != null && articleRepository.existsBySolutionIdAndDeletedFalse(solutionUuid)) {
                log.info("KB article already exists for solution: {}, skipping", solutionUuid);
                return;
            }

            KnowledgeArticle article = KnowledgeArticle.builder()
                    .solutionId(solutionUuid)
                    .ticketId(ticketUuid)
                    .title(title)
                    .content(content)
                    .createdBy(createdBy)
                    .status(ArticleStatus.DRAFT)
                    .visibility(Visibility.ORGANIZATION)
                    .version(1)
                    .deleted(false)
                    .build();

            articleRepository.save(article);
            log.info("Created DRAFT knowledge article {} from approved solution: {}", article.getArticleId(), solutionUuid);
        } catch (Exception e) {
            log.error("Error creating knowledge article from solution.approved event: {}", e.getMessage(), e);
        }
    }

    private UUID parseUuid(String value) {
        if (!StringUtils.hasText(value)) return null;
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            log.warn("Could not parse UUID: {}", value);
            return null;
        }
    }

    private UUID resolveCreatedBy(SolutionApprovedEvent event) {
        if (event.getContributorIds() != null && !event.getContributorIds().isEmpty()) {
            UUID id = parseUuid(event.getContributorIds().get(0));
            if (id != null) return id;
        }
        return UUID.randomUUID();
    }
}
