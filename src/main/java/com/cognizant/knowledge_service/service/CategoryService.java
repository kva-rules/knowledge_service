package com.cognizant.knowledge_service.service;

import com.cognizant.knowledge_service.dto.request.CategoryRequestDTO;
import com.cognizant.knowledge_service.dto.response.CategoryResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponseDTO createCategory(CategoryRequestDTO request);

    CategoryResponseDTO updateCategory(UUID categoryId, CategoryRequestDTO request);

    CategoryResponseDTO getCategoryById(UUID categoryId);

    List<CategoryResponseDTO> getAllCategories();

    List<CategoryResponseDTO> getSubCategories(UUID parentId);

    void deleteCategory(UUID categoryId);
}
