package com.cognizant.knowledge_service.mapper;

import com.cognizant.knowledge_service.dto.response.RatingResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeRating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RatingMapper {

    @Mapping(source = "article.articleId", target = "articleId")
    RatingResponseDTO toResponseDTO(KnowledgeRating rating);
}
