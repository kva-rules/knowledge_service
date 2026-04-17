package com.cognizant.knowledge_service.service;

import java.util.UUID;

public interface ViewService {

    void trackView(UUID articleId, UUID userId);

    long getViewCount(UUID articleId);
}
