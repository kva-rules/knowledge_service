package com.cognizant.knowledge_service.controller;

import com.cognizant.knowledge_service.dto.request.TagRequestDTO;
import com.cognizant.knowledge_service.dto.response.ApiResponseDTO;
import com.cognizant.knowledge_service.dto.response.TagResponseDTO;
import com.cognizant.knowledge_service.service.TagService;
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
public class TagController {

    private final TagService tagService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponseDTO<TagResponseDTO>> createTag(@Valid @RequestBody TagRequestDTO request) {
        TagResponseDTO tag = tagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success("Tag created successfully", tag));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<TagResponseDTO>> getTagById(@PathVariable UUID id) {
        TagResponseDTO tag = tagService.getTagById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(tag));
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<TagResponseDTO>>> getAllTags() {
        List<TagResponseDTO> tags = tagService.getAllTags();
        return ResponseEntity.ok(ApiResponseDTO.success(tags));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Void>> deleteTag(@PathVariable UUID id) {
        tagService.deleteTag(id);
        return ResponseEntity.ok(ApiResponseDTO.success("Tag deleted successfully", null));
    }
}
