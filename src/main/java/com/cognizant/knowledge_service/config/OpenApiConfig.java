package com.cognizant.knowledge_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 configuration for the Knowledge Service.
 *
 * <p>Uses the {@code bearerAuth} security scheme name (HTTP / bearer / JWT) so it
 * matches the convention used by every other service and the gateway aggregator.
 * The {@code swagger_verify.sh} script asserts {@code components.securitySchemes.bearerAuth.scheme == "bearer"}.</p>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String schemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Knowledge Service API")
                        .version("1.0.0")
                        .description("Knowledge Service Microservice for managing knowledge articles, categories, tags, and ratings")
                        .contact(new Contact()
                                .name("Knowledge Service Team")
                                .email("support@cognizant.com")))
                .addSecurityItem(new SecurityRequirement().addList(schemeName))
                .components(new Components()
                        .addSecuritySchemes(schemeName,
                                new SecurityScheme()
                                        .name(schemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste the `accessToken` returned by POST /api/auth/login")));
    }
}
