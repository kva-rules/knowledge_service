package com.cognizant.knowledge_service.domain;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity(name = "article_tags")
@Data
public class articleTags {
    @Id
    @GeneratedValue
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID articleId;

    @Column(nullable = false)
    private UUID tagId;

}


