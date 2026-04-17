package com.cognizant.knowledge_service.service.impl;

import com.cognizant.knowledge_service.dto.request.CategoryRequestDTO;
import com.cognizant.knowledge_service.dto.response.CategoryResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeCategory;
import com.cognizant.knowledge_service.exception.DuplicateResourceException;
import com.cognizant.knowledge_service.exception.ResourceNotFoundException;
import com.cognizant.knowledge_service.mapper.CategoryMapper;
import com.cognizant.knowledge_service.repository.KnowledgeArticleRepository;
import com.cognizant.knowledge_service.repository.KnowledgeCategoryRepository;
import com.cognizant.knowledge_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final KnowledgeCategoryRepository categoryRepository;
    private final KnowledgeArticleRepository articleRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        if (categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new DuplicateResourceException("Category already exists with name: " + request.getCategoryName());
        }

        KnowledgeCategory category = KnowledgeCategory.builder()
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .parentId(request.getParentId())
                .build();

        category = categoryRepository.save(category);
        log.info("Category created: {}", category.getCategoryId());
        return categoryMapper.toResponseDTO(category);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(UUID categoryId, CategoryRequestDTO request) {
        KnowledgeCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));

        if (!category.getCategoryName().equals(request.getCategoryName()) &&
                categoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new DuplicateResourceException("Category already exists with name: " + request.getCategoryName());
        }

        category.setCategoryName(request.getCategoryName());
        category.setDescription(request.getDescription());
        category.setParentId(request.getParentId());

        category = categoryRepository.save(category);
        log.info("Category updated: {}", categoryId);
        return categoryMapper.toResponseDTO(category);
    }

    @Override
    public CategoryResponseDTO getCategoryById(UUID categoryId) {
        KnowledgeCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        return categoryMapper.toResponseDTO(category);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryResponseDTO> getSubCategories(UUID parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(categoryMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCategory(UUID categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }

        long articleCount = articleRepository.countByCategoryId(categoryId);
        if (articleCount > 0) {
            throw new IllegalStateException("Cannot delete category with existing articles");
        }

        categoryRepository.deleteById(categoryId);
        log.info("Category deleted: {}", categoryId);
    }
}
