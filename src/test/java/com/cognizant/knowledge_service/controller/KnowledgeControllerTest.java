package com.cognizant.knowledge_service.controller;

import com.cognizant.knowledge_service.dto.request.ArticleRequestDTO;
import com.cognizant.knowledge_service.dto.response.ArticleResponseDTO;
import com.cognizant.knowledge_service.dto.response.PageResponseDTO;
import com.cognizant.knowledge_service.enums.ArticleStatus;
import com.cognizant.knowledge_service.enums.Visibility;
import com.cognizant.knowledge_service.security.JwtTokenProvider;
import com.cognizant.knowledge_service.service.KnowledgeService;
import com.cognizant.knowledge_service.service.ViewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KnowledgeController.class)
class KnowledgeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private KnowledgeService knowledgeService;

    @MockBean
    private ViewService viewService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UUID articleId;
    private UUID userId;
    private ArticleRequestDTO articleRequest;
    private ArticleResponseDTO articleResponse;

    @BeforeEach
    void setUp() {
        articleId = UUID.randomUUID();
        userId = UUID.randomUUID();

        articleRequest = ArticleRequestDTO.builder()
                .title("Test Article")
                .content("Test Content")
                .tags(Set.of("tag1", "tag2"))
                .visibility(Visibility.ORGANIZATION)
                .build();

        articleResponse = ArticleResponseDTO.builder()
                .articleId(articleId)
                .title("Test Article")
                .content("Test Content")
                .status(ArticleStatus.DRAFT)
                .visibility(Visibility.ORGANIZATION)
                .version(1)
                .averageRating(4.5)
                .totalViews(100L)
                .build();
    }

    @Test
    @DisplayName("Should create article successfully")
    @WithMockUser(roles = "MANAGER")
    void createArticle_Success() throws Exception {
        when(knowledgeService.createArticle(any(ArticleRequestDTO.class), any(UUID.class)))
                .thenReturn(articleResponse);

        mockMvc.perform(post("/api/knowledge")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .content(objectMapper.writeValueAsString(articleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Test Article"));
    }

    @Test
    @DisplayName("Should get article by ID successfully")
    @WithMockUser
    void getArticleById_Success() throws Exception {
        when(knowledgeService.getArticleById(articleId)).thenReturn(articleResponse);

        mockMvc.perform(get("/api/knowledge/{id}", articleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.articleId").value(articleId.toString()));
    }

    @Test
    @DisplayName("Should get all articles with pagination")
    @WithMockUser
    void getAllArticles_Success() throws Exception {
        PageResponseDTO<ArticleResponseDTO> pageResponse = PageResponseDTO.<ArticleResponseDTO>builder()
                .content(List.of(articleResponse))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .build();

        when(knowledgeService.getAllArticles(any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/knowledge")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @DisplayName("Should search articles successfully")
    @WithMockUser
    void searchArticles_Success() throws Exception {
        PageResponseDTO<ArticleResponseDTO> pageResponse = PageResponseDTO.<ArticleResponseDTO>builder()
                .content(List.of(articleResponse))
                .pageNumber(0)
                .pageSize(10)
                .totalElements(1)
                .totalPages(1)
                .first(true)
                .last(true)
                .build();

        when(knowledgeService.searchArticles(any(), any(), any(), any(), any())).thenReturn(pageResponse);

        mockMvc.perform(get("/api/knowledge/search")
                        .param("keyword", "test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should update article successfully")
    @WithMockUser(roles = "MANAGER")
    void updateArticle_Success() throws Exception {
        when(knowledgeService.updateArticle(eq(articleId), any(ArticleRequestDTO.class), any(UUID.class)))
                .thenReturn(articleResponse);

        mockMvc.perform(put("/api/knowledge/{id}", articleId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .content(objectMapper.writeValueAsString(articleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should publish article successfully")
    @WithMockUser(roles = "MANAGER")
    void publishArticle_Success() throws Exception {
        articleResponse.setStatus(ArticleStatus.PUBLISHED);
        when(knowledgeService.publishArticle(eq(articleId), any(UUID.class))).thenReturn(articleResponse);

        mockMvc.perform(put("/api/knowledge/{id}/publish", articleId)
                        .with(csrf())
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"));
    }

    @Test
    @DisplayName("Should archive article successfully")
    @WithMockUser(roles = "MANAGER")
    void archiveArticle_Success() throws Exception {
        articleResponse.setStatus(ArticleStatus.ARCHIVED);
        when(knowledgeService.archiveArticle(eq(articleId), any(UUID.class))).thenReturn(articleResponse);

        mockMvc.perform(put("/api/knowledge/{id}/archive", articleId)
                        .with(csrf())
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("ARCHIVED"));
    }

    @Test
    @DisplayName("Should delete article successfully")
    @WithMockUser(roles = "ADMIN")
    void deleteArticle_Success() throws Exception {
        mockMvc.perform(delete("/api/knowledge/{id}", articleId)
                        .with(csrf())
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should track view successfully")
    @WithMockUser
    void trackView_Success() throws Exception {
        mockMvc.perform(post("/api/knowledge/{articleId}/view", articleId)
                        .with(csrf())
                        .header("X-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should return 400 for invalid article request")
    @WithMockUser(roles = "MANAGER")
    void createArticle_ValidationError() throws Exception {
        ArticleRequestDTO invalidRequest = ArticleRequestDTO.builder()
                .title("")  // Empty title should fail validation
                .content("Test Content")
                .build();

        mockMvc.perform(post("/api/knowledge")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", userId.toString())
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
