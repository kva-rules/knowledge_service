package com.cognizant.knowledge_service.controller;

import com.cognizant.knowledge_service.dto.request.TagRequestDTO;
import com.cognizant.knowledge_service.dto.response.ApiResponseDTO;
import com.cognizant.knowledge_service.dto.response.TagResponseDTO;
import com.cognizant.knowledge_service.service.TagService;
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
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "Knowledge Tags", description = "Article tagging")
public class TagController {

    private final TagService tagService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new tag", description = "Creates a tag usable on KB articles")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Tag created"),
            @ApiResponse(responseCode = "400", description = "Invalid payload"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "409", description = "Tag already exists")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<TagResponseDTO>> createTag(@Valid @RequestBody TagRequestDTO request) {
        TagResponseDTO tag = tagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Tag created successfully", tag));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tag by ID", description = "Fetches a single tag by its UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag found"),
            @ApiResponse(responseCode = "404", description = "Tag not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<TagResponseDTO>> getTagById(
            @Parameter(description = "Tag UUID") @PathVariable UUID id) {
        TagResponseDTO tag = tagService.getTagById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(tag));
    }

    @GetMapping
    @Operation(summary = "List all tags", description = "Returns every tag defined in the system")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tags returned"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<List<TagResponseDTO>>> getAllTags() {
        List<TagResponseDTO> tags = tagService.getAllTags();
        return ResponseEntity.ok(ApiResponseDTO.success(tags));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a tag", description = "Removes a tag (admin only)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tag deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Tag not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponseDTO<Void>> deleteTag(
            @Parameter(description = "Tag UUID") @PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Tag deleted successfully", null));
    }
}
