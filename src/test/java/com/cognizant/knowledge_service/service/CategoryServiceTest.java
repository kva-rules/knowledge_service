package com.cognizant.knowledge_service.service;

import com.cognizant.knowledge_service.dto.request.CategoryRequestDTO;
import com.cognizant.knowledge_service.dto.response.CategoryResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeCategory;
import com.cognizant.knowledge_service.exception.DuplicateResourceException;
import com.cognizant.knowledge_service.exception.ResourceNotFoundException;
import com.cognizant.knowledge_service.mapper.CategoryMapper;
import com.cognizant.knowledge_service.repository.KnowledgeArticleRepository;
import com.cognizant.knowledge_service.repository.KnowledgeCategoryRepository;
import com.cognizant.knowledge_service.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private KnowledgeCategoryRepository categoryRepository;

    @Mock
    private KnowledgeArticleRepository articleRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private UUID categoryId;
    private KnowledgeCategory category;
    private CategoryRequestDTO categoryRequest;
    private CategoryResponseDTO categoryResponse;

    @BeforeEach
    void setUp() {
        categoryId = UUID.randomUUID();

        category = KnowledgeCategory.builder()
                .categoryId(categoryId)
                .categoryName("Test Category")
                .description("Test Description")
                .build();

        categoryRequest = CategoryRequestDTO.builder()
                .categoryName("Test Category")
                .description("Test Description")
                .build();

        categoryResponse = CategoryResponseDTO.builder()
                .categoryId(categoryId)
                .categoryName("Test Category")
                .description("Test Description")
                .build();
    }

    @Test
    @DisplayName("Should create category successfully")
    void createCategory_Success() {
        when(categoryRepository.existsByCategoryName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(KnowledgeCategory.class))).thenReturn(category);
        when(categoryMapper.toResponseDTO(any(KnowledgeCategory.class))).thenReturn(categoryResponse);

        CategoryResponseDTO result = categoryService.createCategory(categoryRequest);

        assertNotNull(result);
        assertEquals("Test Category", result.getCategoryName());
        verify(categoryRepository).save(any(KnowledgeCategory.class));
    }

    @Test
    @DisplayName("Should throw exception when category name already exists")
    void createCategory_DuplicateName() {
        when(categoryRepository.existsByCategoryName(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                categoryService.createCategory(categoryRequest));

        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get category by ID successfully")
    void getCategoryById_Success() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryMapper.toResponseDTO(category)).thenReturn(categoryResponse);

        CategoryResponseDTO result = categoryService.getCategoryById(categoryId);

        assertNotNull(result);
        assertEquals(categoryId, result.getCategoryId());
    }

    @Test
    @DisplayName("Should throw exception when category not found")
    void getCategoryById_NotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                categoryService.getCategoryById(categoryId));
    }

    @Test
    @DisplayName("Should get all categories successfully")
    void getAllCategories_Success() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(categoryMapper.toResponseDTO(any(KnowledgeCategory.class))).thenReturn(categoryResponse);

        List<CategoryResponseDTO> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should update category successfully")
    void updateCategory_Success() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.existsByCategoryName(anyString())).thenReturn(false);
        when(categoryRepository.save(any(KnowledgeCategory.class))).thenReturn(category);
        when(categoryMapper.toResponseDTO(any(KnowledgeCategory.class))).thenReturn(categoryResponse);

        CategoryResponseDTO result = categoryService.updateCategory(categoryId, categoryRequest);

        assertNotNull(result);
        verify(categoryRepository).save(any(KnowledgeCategory.class));
    }

    @Test
    @DisplayName("Should delete category successfully")
    void deleteCategory_Success() {
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(articleRepository.countByCategoryId(categoryId)).thenReturn(0L);

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository).deleteById(categoryId);
    }

    @Test
    @DisplayName("Should throw exception when deleting category with articles")
    void deleteCategory_HasArticles() {
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(articleRepository.countByCategoryId(categoryId)).thenReturn(5L);

        assertThrows(IllegalStateException.class, () ->
                categoryService.deleteCategory(categoryId));

        verify(categoryRepository, never()).deleteById(any());
    }
}
