package com.cognizant.knowledge_service.domain;
import com.cognizant.knowledge_service.enums.ArticleStatus;
import com.cognizant.knowledge_service.enums.visibility;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity(name = "knowledge_articles")
@Data
public class KnowledgeArticles {
    @Id
    @GeneratedValue
    @Column(name = "articleId", updatable = false, nullable = false)
    private UUID articleId;

    @Column(nullable = false)
    private UUID ticketId;

    @Column(nullable = false)
    private UUID solutionId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private UUID categoryId;

    @Column(nullable = false)
    private UUID createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArticleStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private visibility visibility;

    @Column(nullable = false)
    private int version;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Date updatedAt;

    @Column(nullable = false)
    private boolean deleted;

}
