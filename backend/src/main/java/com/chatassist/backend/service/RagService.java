package com.chatassist.backend.service;

import com.chatassist.backend.model.DocumentChunk;
import com.chatassist.backend.repo.DocumentChunkRepository;
import com.chatassist.backend.util.SimilarityUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;

    public RagService(DocumentChunkRepository chunkRepository,
                      EmbeddingService embeddingService) {
        this.chunkRepository = chunkRepository;
        this.embeddingService = embeddingService;
    }

    public List<DocumentChunk> retrieveRelevantChunks(String question) {

        List<Double> queryEmbedding = embeddingService.embed(question);

        return chunkRepository.findAll().stream()
            .map(chunk -> Map.entry(
                chunk,
                SimilarityUtil.cosine(
                    queryEmbedding,
                    parseEmbedding(chunk.getEmbedding())
                )
            ))
            .filter(e -> e.getValue() > 0.15)   // similarity threshold
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(3)                           // top-K
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    private List<Double> parseEmbedding(String embeddingStr) {
        String cleaned = embeddingStr.replace("[", "").replace("]", "");
        String[] parts = cleaned.split(",");

        List<Double> vec = new ArrayList<>();
        for (String p : parts) {
            vec.add(Double.parseDouble(p.trim()));
        }
        return vec;
    }
}
