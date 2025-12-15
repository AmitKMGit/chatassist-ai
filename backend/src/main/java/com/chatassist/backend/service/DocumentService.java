package com.chatassist.backend.service;

import com.chatassist.backend.model.Document;
import com.chatassist.backend.model.DocumentChunk;
import com.chatassist.backend.model.User;
import com.chatassist.backend.repo.DocumentChunkRepository;
import com.chatassist.backend.repo.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;

    public DocumentService(DocumentRepository documentRepository,
                           DocumentChunkRepository chunkRepository,
                           EmbeddingService embeddingService) {
        this.documentRepository = documentRepository;
        this.chunkRepository = chunkRepository;
        this.embeddingService = embeddingService;
    }

    public void upload(User user, String name, String content) {

        // 1. Save document
        Document doc = new Document();
        doc.setName(name);
        doc.setUser(user);
        documentRepository.save(doc);

        // 2. Chunk text
        List<String> chunks = chunkText(content);

        // 3. Generate embeddings + save chunks
        for (int i = 0; i < chunks.size(); i++) {
            String chunkText = chunks.get(i);

            List<Double> embedding =
                    embeddingService.embed(chunkText);

            DocumentChunk chunk = new DocumentChunk();
            chunk.setDocument(doc);
            chunk.setChunkIndex(i);
            chunk.setContent(chunkText);
            chunk.setEmbedding(embedding.toString());

            chunkRepository.save(chunk);
        }
    }

    private List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();
        int size = 100;
        int overlap = 20;



        for (int i = 0; i < text.length(); i += size - overlap) {
            chunks.add(text.substring(
                    i,
                    Math.min(text.length(), i + size)
            ));
        }
        return chunks;
    }
}
