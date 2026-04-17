package com.cognizant.knowledge_service.mapper;

import com.cognizant.knowledge_service.dto.response.VersionResponseDTO;
import com.cognizant.knowledge_service.entity.KnowledgeVersion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VersionMapper {

    @Mapping(source = "article.articleId", target = "articleId")
    VersionResponseDTO toResponseDTO(KnowledgeVersion version);
}
