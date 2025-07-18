package com.example.SpringAI.service;

import com.example.SpringAI.util.TextSplitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class PDFService {
    @Autowired
    private TextSplitter textSplitter;
    @Autowired private EmbeddingService embeddingService;
    @Autowired private RedisVectorService redisVectorService;

    public String processAndStoreDocument(MultipartFile file) {
        System.out.println(("Inside PDFService processAndStoreDocument()"));
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            List<String> chunks = textSplitter.split(text);
            String id = String.valueOf(UUID.randomUUID());
            for (int i = 0; i < chunks.size(); i++) {
                String chunkText = chunks.get(i);
                float[] embedding = embeddingService.embedText(chunkText);

                redisVectorService.storeChunk(id, i, chunkText, embedding);
            }
            return id; // document ID
        } catch (IOException e) {
            throw new RuntimeException("Error reading PDF", e);
        }
    }
}
