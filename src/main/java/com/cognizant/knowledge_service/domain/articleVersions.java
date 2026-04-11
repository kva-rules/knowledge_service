package com.cognizant.knowledge_service.domain;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity(name = "article_versions")
@Data
public class articleVersions {
    @Id
    @GeneratedValue
    @Column(name = "versionId", updatable = false, nullable = false)
    private UUID versionId;

    @Column(nullable = false)
    private UUID articleId;

    @Column(nullable = false)
    private int versionNumber;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private UUID editedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;
}
