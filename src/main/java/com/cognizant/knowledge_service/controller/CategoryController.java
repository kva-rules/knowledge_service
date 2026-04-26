package com.cognizant.knowledge_service.controller;

import com.cognizant.knowledge_service.dto.request.CategoryRequestDTO;
import com.cognizant.knowledge_service.dto.response.ApiResponseDTO;
import com.cognizant.knowledge_service.dto.response.CategoryResponseDTO;
import com.cognizant.knowledge_service.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Knowledge Categories", description = "Hierarchical categorization of articles")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new category", description = "Creates a top-level or child category (admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category created"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Category already exists")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<CategoryResponseDTO>> createCategory(
            @Valid @RequestBody CategoryRequestDTO request) {
        CategoryResponseDTO category = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Category created successfully", category));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Fetches a single category by its UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<CategoryResponseDTO>> getCategoryById(
            @Parameter(description = "Category UUID") @PathVariable UUID id) {
        CategoryResponseDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(category));
    }

    @GetMapping
    @Operation(summary = "List all categories", description = "Returns every category in the hierarchy")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categories returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<List<CategoryResponseDTO>>> getAllCategories() {
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponseDTO.success(categories));
    }

    @GetMapping("/{id}/subcategories")
    @Operation(summary = "List subcategories", description = "Returns direct child categories of the given parent")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subcategories returned"),
            @ApiResponse(responseCode = "404", description = "Parent category not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<List<CategoryResponseDTO>>> getSubCategories(
            @Parameter(description = "Parent category UUID") @PathVariable UUID id) {
        List<CategoryResponseDTO> subCategories = categoryService.getSubCategories(id);
        return ResponseEntity.ok(ApiResponseDTO.success(subCategories));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a category", description = "Updates name or parent of an existing category")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category updated"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<CategoryResponseDTO>> updateCategory(
            @Parameter(description = "Category UUID") @PathVariable UUID id,
            @Valid @RequestBody CategoryRequestDTO request) {
        CategoryResponseDTO category = categoryService.updateCategory(id, request);
        return ResponseEntity.ok(ApiResponseDTO.success("Category updated successfully", category));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a category", description = "Removes a category (admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Category deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<Void>> deleteCategory(
            @Parameter(description = "Category UUID") @PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Category deleted successfully", null));
    }
}
