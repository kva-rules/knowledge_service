
package com.cognizant.knowledge_service.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity(name = "knowledge_categories")
@Data
public class KnowledgeCategory {

    @Id
    @GeneratedValue
    @Column(name = "categoryId", updatable = false, nullable = false)
    private UUID categoryId;

    @Column(nullable = false, unique = true)
    private String categoryName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;
}