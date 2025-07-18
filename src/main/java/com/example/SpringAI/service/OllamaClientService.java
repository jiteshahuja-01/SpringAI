package com.example.SpringAI.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
@Service
public class OllamaClientService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${ollama.base-url}")
    private String OLLAMA_BASE_URL;

    public String generateAnswer(String prompt) {
        Map<String, Object> req = Map.of(
                "model", "tinyllama",
                "prompt", prompt,
                "stream", false,
                "temperature", 0.0
        );
        ResponseEntity<Map> res = restTemplate.postForEntity(OLLAMA_BASE_URL + "/api/generate", req, Map.class);
        return (String) res.getBody().get("response");
    }

}
