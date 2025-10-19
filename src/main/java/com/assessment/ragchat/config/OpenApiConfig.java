package com.assessment.ragchat.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi ragChatApi() {
        return GroupedOpenApi.builder()
                .group("rag-chat")
                .packagesToScan("com.assessment.ragchat.controller")
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("RAG Chat Storage API")
                        .version("v1")
                        .description("API documentation for RAG Chat storage service"));
    }
}