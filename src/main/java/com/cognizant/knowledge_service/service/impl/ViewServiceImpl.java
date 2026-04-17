package com.cognizant.knowledge_service.service.impl;

import com.cognizant.knowledge_service.entity.KnowledgeArticle;
import com.cognizant.knowledge_service.entity.KnowledgeView;
import com.cognizant.knowledge_service.enums.ActivityAction;
import com.cognizant.knowledge_service.exception.ResourceNotFoundException;
import com.cognizant.knowledge_service.repository.KnowledgeArticleRepository;
import com.cognizant.knowledge_service.repository.KnowledgeViewRepository;
import com.cognizant.knowledge_service.service.ActivityLogService;
import com.cognizant.knowledge_service.service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViewServiceImpl implements ViewService {

    private final KnowledgeViewRepository viewRepository;
    private final KnowledgeArticleRepository articleRepository;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional
    public void trackView(UUID articleId, UUID userId) {
        KnowledgeArticle article = articleRepository.findByArticleIdAndDeletedFalse(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found with id: " + articleId));

        if (!viewRepository.existsByArticleArticleIdAndUserId(articleId, userId)) {
            KnowledgeView view = KnowledgeView.builder()
                    .article(article)
                    .userId(userId)
                    .build();

            viewRepository.save(view);
            activityLogService.logActivity(article, ActivityAction.VIEWED, userId, "Article viewed");
            log.info("View tracked for article {} by user {}", articleId, userId);
        }
    }

    @Override
    public long getViewCount(UUID articleId) {
        return viewRepository.countByArticleId(articleId);
    }
}
