package com.cognizant.knowledge_service.service.impl;

import com.cognizant.knowledge_service.dto.request.TagRequestDTO;
import com.cognizant.knowledge_service.dto.response.TagResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeTag;
import com.cognizant.knowledge_service.exception.DuplicateResourceException;
import com.cognizant.knowledge_service.exception.ResourceNotFoundException;
import com.cognizant.knowledge_service.mapper.TagMapper;
import com.cognizant.knowledge_service.repository.KnowledgeTagRepository;
import com.cognizant.knowledge_service.service.TagService;
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
public class TagServiceImpl implements TagService {

    private final KnowledgeTagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    @Transactional
    public TagResponseDTO createTag(TagRequestDTO request) {
        if (tagRepository.existsByTagName(request.getTagName())) {
            throw new DuplicateResourceException("Tag already exists with name: " + request.getTagName());
        }

        KnowledgeTag tag = KnowledgeTag.builder()
                .tagName(request.getTagName())
                .build();

        tag = tagRepository.save(tag);
        log.info("Tag created: {}", tag.getTagId());
        return tagMapper.toResponseDTO(tag);
    }

    @Override
    public TagResponseDTO getTagById(UUID tagId) {
        KnowledgeTag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found with id: " + tagId));
        return tagMapper.toResponseDTO(tag);
    }

    @Override
    public List<TagResponseDTO> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tagMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteTag(UUID tagId) {
        if (!tagRepository.existsById(tagId)) {
            throw new ResourceNotFoundException("Tag not found with id: " + tagId);
        }
        tagRepository.deleteById(tagId);
        log.info("Tag deleted: {}", tagId);
    }
}
