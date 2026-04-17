package com.cognizant.knowledge_service.kafka;

import com.cognizant.knowledge_service.kafka.event.KnowledgeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KnowledgeEventProducer {

    private final KafkaTemplate<String, KnowledgeEvent> kafkaTemplate;

    private static final String TOPIC_KNOWLEDGE_CREATED = "knowledge.created";
    private static final String TOPIC_KNOWLEDGE_UPDATED = "knowledge.updated";
    private static final String TOPIC_KNOWLEDGE_PUBLISHED = "knowledge.published";
    private static final String TOPIC_KNOWLEDGE_RATED = "knowledge.rated";

    public void sendKnowledgeCreatedEvent(UUID articleId, String title, UUID createdBy) {
        KnowledgeEvent event = KnowledgeEvent.builder()
                .eventType("KNOWLEDGE_CREATED")
                .articleId(articleId)
                .title(title)
                .performedBy(createdBy)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(TOPIC_KNOWLEDGE_CREATED, articleId.toString(), event);
        log.info("Sent knowledge.created event for article: {}", articleId);
    }

    public void sendKnowledgeUpdatedEvent(UUID articleId, String title, UUID updatedBy, Integer version) {
        KnowledgeEvent event = KnowledgeEvent.builder()
                .eventType("KNOWLEDGE_UPDATED")
                .articleId(articleId)
                .title(title)
                .performedBy(updatedBy)
                .version(version)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(TOPIC_KNOWLEDGE_UPDATED, articleId.toString(), event);
        log.info("Sent knowledge.updated event for article: {}", articleId);
    }

    public void sendKnowledgePublishedEvent(UUID articleId, String title, UUID publishedBy) {
        KnowledgeEvent event = KnowledgeEvent.builder()
                .eventType("KNOWLEDGE_PUBLISHED")
                .articleId(articleId)
                .title(title)
                .performedBy(publishedBy)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(TOPIC_KNOWLEDGE_PUBLISHED, articleId.toString(), event);
        log.info("Sent knowledge.published event for article: {}", articleId);
    }

    public void sendKnowledgeRatedEvent(UUID articleId, UUID userId, Integer rating) {
        KnowledgeEvent event = KnowledgeEvent.builder()
                .eventType("KNOWLEDGE_RATED")
                .articleId(articleId)
                .performedBy(userId)
                .rating(rating)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(TOPIC_KNOWLEDGE_RATED, articleId.toString(), event);
        log.info("Sent knowledge.rated event for article: {}", articleId);
    }
}
