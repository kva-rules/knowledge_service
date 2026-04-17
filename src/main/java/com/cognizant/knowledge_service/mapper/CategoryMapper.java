package com.cognizant.knowledge_service.mapper;

import com.cognizant.knowledge_service.dto.response.CategoryResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeCategory;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    CategoryResponseDTO toResponseDTO(KnowledgeCategory category);
}
