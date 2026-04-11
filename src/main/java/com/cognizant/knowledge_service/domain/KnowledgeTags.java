
package com.cognizant.knowledge_service.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;
import java.util.UUID;

@Entity(name = "knowledge_tags")
@Data
public class KnowledgeTags{

    @Id
    @GeneratedValue
    @Column(name = "tagId", updatable = false, nullable = false)
    private UUID tagId;

    @Column(nullable = false, unique = true)
    private String tagName;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;
}