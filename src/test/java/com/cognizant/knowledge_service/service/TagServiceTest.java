package com.cognizant.knowledge_service.service;

import com.cognizant.knowledge_service.dto.request.TagRequestDTO;
import com.cognizant.knowledge_service.dto.response.TagResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeTag;
import com.cognizant.knowledge_service.exception.DuplicateResourceException;
import com.cognizant.knowledge_service.exception.ResourceNotFoundException;
import com.cognizant.knowledge_service.mapper.TagMapper;
import com.cognizant.knowledge_service.repository.KnowledgeTagRepository;
import com.cognizant.knowledge_service.service.impl.TagServiceImpl;
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
class TagServiceTest {

    @Mock
    private KnowledgeTagRepository tagRepository;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private TagServiceImpl tagService;

    private UUID tagId;
    private KnowledgeTag tag;
    private TagRequestDTO tagRequest;
    private TagResponseDTO tagResponse;

    @BeforeEach
    void setUp() {
        tagId = UUID.randomUUID();

        tag = KnowledgeTag.builder()
                .tagId(tagId)
                .tagName("test-tag")
                .build();

        tagRequest = TagRequestDTO.builder()
                .tagName("test-tag")
                .build();

        tagResponse = TagResponseDTO.builder()
                .tagId(tagId)
                .tagName("test-tag")
                .build();
    }

    @Test
    @DisplayName("Should create tag successfully")
    void createTag_Success() {
        when(tagRepository.existsByTagName(anyString())).thenReturn(false);
        when(tagRepository.save(any(KnowledgeTag.class))).thenReturn(tag);
        when(tagMapper.toResponseDTO(any(KnowledgeTag.class))).thenReturn(tagResponse);

        TagResponseDTO result = tagService.createTag(tagRequest);

        assertNotNull(result);
        assertEquals("test-tag", result.getTagName());
        verify(tagRepository).save(any(KnowledgeTag.class));
    }

    @Test
    @DisplayName("Should throw exception when tag name already exists")
    void createTag_DuplicateName() {
        when(tagRepository.existsByTagName(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () ->
                tagService.createTag(tagRequest));

        verify(tagRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get tag by ID successfully")
    void getTagById_Success() {
        when(tagRepository.findById(tagId)).thenReturn(Optional.of(tag));
        when(tagMapper.toResponseDTO(tag)).thenReturn(tagResponse);

        TagResponseDTO result = tagService.getTagById(tagId);

        assertNotNull(result);
        assertEquals(tagId, result.getTagId());
    }

    @Test
    @DisplayName("Should throw exception when tag not found")
    void getTagById_NotFound() {
        when(tagRepository.findById(tagId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                tagService.getTagById(tagId));
    }

    @Test
    @DisplayName("Should get all tags successfully")
    void getAllTags_Success() {
        when(tagRepository.findAll()).thenReturn(List.of(tag));
        when(tagMapper.toResponseDTO(any(KnowledgeTag.class))).thenReturn(tagResponse);

        List<TagResponseDTO> result = tagService.getAllTags();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should delete tag successfully")
    void deleteTag_Success() {
        when(tagRepository.existsById(tagId)).thenReturn(true);

        tagService.deleteTag(tagId);

        verify(tagRepository).deleteById(tagId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent tag")
    void deleteTag_NotFound() {
        when(tagRepository.existsById(tagId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () ->
                tagService.deleteTag(tagId));

        verify(tagRepository, never()).deleteById(any());
    }
}
