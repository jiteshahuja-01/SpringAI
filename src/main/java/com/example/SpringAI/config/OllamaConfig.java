package com.example.SpringAI.config;

import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.ollama.api.OllamaApi;
//import org.springframework.ai.ollama.embedding.OllamaEmbeddingModel;

@Configuration
public class OllamaConfig {

    @Value("${ollama.base-url}")
    private String baseUrl;

    @Bean
    public OllamaApi ollamaApi() {
        return OllamaApi.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public EmbeddingModel ollamaEmbeddingModel(OllamaApi ollamaApi) {
        return OllamaEmbeddingModel.builder().ollamaApi(ollamaApi).build();
    }
}
