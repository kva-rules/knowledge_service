package com.cognizant.knowledge_service.mapper;

import com.cognizant.knowledge_service.dto.response.ArticleResponseDTO;
import com.cognizant.knowledge_service.dto.response.CategoryResponseDTO;
import com.cognizant.knowledge_service.dto.response.TagResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeArticle;
import com.cognizant.knowledge_service.entity.KnowledgeCategory;
import com.cognizant.knowledge_service.entity.KnowledgeTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArticleMapper {

    @Mapping(target = "averageRating", ignore = true)
    @Mapping(target = "totalViews", ignore = true)
    ArticleResponseDTO toResponseDTO(KnowledgeArticle article);

    CategoryResponseDTO toCategoryResponseDTO(KnowledgeCategory category);

    TagResponseDTO toTagResponseDTO(KnowledgeTag tag);

    Set<TagResponseDTO> toTagResponseDTOSet(Set<KnowledgeTag> tags);
}
