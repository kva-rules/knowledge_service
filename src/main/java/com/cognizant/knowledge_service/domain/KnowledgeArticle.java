package com.cognizant.knowledge_service.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.ToString;

import java.util.UUID;

@Entity ( name = "knowledge_article")
@ToString
public class KnowledgeArticle {
    @Id
    @Column (name = "id")
    @GeneratedValue
    public UUID id;

    @Column (name = "name")
    public String name;
}
