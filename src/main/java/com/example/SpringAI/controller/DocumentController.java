package com.example.SpringAI.controller;

import com.example.SpringAI.service.PDFService;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

@RestController
@RequestMapping("/document")
public class DocumentController {
    @Autowired private PDFService pdfService;
    @Autowired private JedisPool jedisPool;
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>>  uploadDocument(@RequestParam("file") MultipartFile file) {
        String docId = this.pdfService.processAndStoreDocument(file);
        Map<String, String> response = new HashMap<>();
        response.put("document_id", docId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/chunks/{docId}")
    public List<String> getChunks(@PathVariable String docId) {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys("doc:" + docId + ":*");
            List<String> chunks = new ArrayList<>();
            for (String key : keys) {
                String chunk = jedis.hget(key, "chunk");
                chunks.add("Key: " + key + " → " + chunk);
            }
            return chunks;
        }
    }

}
