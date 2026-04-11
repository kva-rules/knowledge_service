package com.cognizant.knowledge_service.domain;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;
import java.util.UUID;

@Entity(name = "article_ratings")
@Data
public class KnowledgeRating {
    @Id
    @GeneratedValue
    @Column(name = "ratingId", updatable = false, nullable = false)
    private UUID ratingId;

    @Column(nullable = false)
    private UUID articleId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private int rating; // e.g., 1–5 stars

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;
}
