package com.cognizant.knowledge_service.dto.request;

import com.cognizant.knowledge_service.enums.Visibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticleRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    private UUID categoryId;

    private Set<String> tags;

    private Visibility visibility;

    private UUID ticketId;

    private UUID solutionId;
}
