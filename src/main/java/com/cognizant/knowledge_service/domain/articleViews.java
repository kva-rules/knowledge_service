package com.cognizant.knowledge_service.domain;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity(name = "article_views")
@Data

public class articleViews {
    @Id
    @GeneratedValue
    @Column(name = "viewId", updatable = false, nullable = false)
    private UUID viewId;

    @Column(nullable = false)
    private UUID articleId;

    @Column(nullable = false)
    private UUID userId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date viewedAt;
}
