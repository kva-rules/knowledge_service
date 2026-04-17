package com.cognizant.knowledge_service.mapper;

import com.cognizant.knowledge_service.dto.response.TagResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeTag;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    TagResponseDTO toResponseDTO(KnowledgeTag tag);
}
