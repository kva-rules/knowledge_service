package com.cognizant.knowledge_service.service;

import com.cognizant.knowledge_service.dto.request.TagRequestDTO;
import com.cognizant.knowledge_service.dto.response.TagResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TagService {

    TagResponseDTO createTag(TagRequestDTO request);

    TagResponseDTO getTagById(UUID tagId);

    List<TagResponseDTO> getAllTags();

    void deleteTag(UUID tagId);
}
