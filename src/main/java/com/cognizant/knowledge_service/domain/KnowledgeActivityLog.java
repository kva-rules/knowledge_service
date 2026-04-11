package com.cognizant.knowledge_service.domain;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import com.cognizant.knowledge_service.enums.Action;
import java.util.Date;
import java.util.UUID;

@Entity(name = "article_activities")
@Data
public class  KnowledgeActivityLog{

    @Id
    @GeneratedValue
    @Column(name = "activityId", updatable = false, nullable = false)
    private UUID activityId;

    @Column(nullable = false)
    private UUID articleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action; // e.g., CREATED, EDITED, PUBLISHED

    @Column(nullable = false)
    private UUID performedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Date createdAt;
}
