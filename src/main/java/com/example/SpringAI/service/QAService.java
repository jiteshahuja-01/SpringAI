package com.example.SpringAI.service;

import com.example.SpringAI.model.Chunk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QAService {
    @Autowired
    private OllamaClientService ollamaClientService;

    public String answer(String question, List<Chunk> chunks) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Answer the following question using the provided context:\n\n");
        prompt.append("Context:\n");
        chunks.forEach(chunk -> prompt.append("- ").append(chunk.getText()).append("\n"));
        prompt.append("\nQuestion:\n").append(question).append("\nAnswer:");
        return ollamaClientService.generateAnswer(prompt.toString());
    }
}

