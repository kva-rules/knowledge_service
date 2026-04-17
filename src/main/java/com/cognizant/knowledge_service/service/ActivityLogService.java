package com.cognizant.knowledge_service.service;

import com.cognizant.knowledge_service.entity.KnowledgeArticle;
import com.cognizant.knowledge_service.enums.ActivityAction;

import java.util.UUID;

public interface ActivityLogService {

    void logActivity(KnowledgeArticle article, ActivityAction action, UUID performedBy, String details);
}
