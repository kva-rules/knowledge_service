package com.cognizant.knowledge_service.service.impl;

import com.cognizant.knowledge_service.entity.KnowledgeActivityLog;
import com.cognizant.knowledge_service.entity.KnowledgeArticle;
import com.cognizant.knowledge_service.enums.ActivityAction;
import com.cognizant.knowledge_service.repository.KnowledgeActivityLogRepository;
import com.cognizant.knowledge_service.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityLogServiceImpl implements ActivityLogService {

    private final KnowledgeActivityLogRepository activityLogRepository;

    @Override
    @Transactional
    public void logActivity(KnowledgeArticle article, ActivityAction action, UUID performedBy, String details) {
        KnowledgeActivityLog activityLog = KnowledgeActivityLog.builder()
                .article(article)
                .action(action)
                .performedBy(performedBy)
                .details(details)
                .build();

        activityLogRepository.save(activityLog);
        log.info("Activity logged: {} for article {} by user {}", action, article.getArticleId(), performedBy);
    }
}
