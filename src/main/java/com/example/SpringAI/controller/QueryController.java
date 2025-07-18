package com.example.SpringAI.controller;

import com.example.SpringAI.model.Chunk;
import com.example.SpringAI.model.QuestionRequest;
import com.example.SpringAI.service.EmbeddingService;
import com.example.SpringAI.service.QAService;
import com.example.SpringAI.service.RedisVectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/query")
public class QueryController {
    @Autowired
    private EmbeddingService embeddingService;
    @Autowired private RedisVectorService redisVectorService;
    @Autowired private QAService qaService;

    @PostMapping("/similar")
    public List<Chunk> findSimilarChunks(@RequestBody String query) {
        float[] vector = embeddingService.embedText(query);
        return redisVectorService.searchSimilarChunks(vector);
    }

    @PostMapping("/ask")
    public String askQuestion(@RequestBody QuestionRequest req) {
        float[] embedding = embeddingService.embedText(req.getQuestion());
        List<Chunk> chunks = redisVectorService.searchSimilarChunks(embedding);
        return qaService.answer(req.getQuestion(), chunks);
    }
}
