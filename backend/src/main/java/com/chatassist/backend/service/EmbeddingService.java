package com.chatassist.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {

    // Fixed vector size
    private static final int DIM = 128;

    public List<Double> embed(String text) {
        double[] vector = new double[DIM];

        // Simple token hashing
        String[] tokens = text.toLowerCase().split("\\W+");

        for (String token : tokens) {
            int hash = Math.abs(token.hashCode());
            int index = hash % DIM;
            vector[index] += 1.0;
        }

        // Normalize vector
        double norm = 0.0;
        for (double v : vector) norm += v * v;
        norm = Math.sqrt(norm);

        List<Double> embedding = new ArrayList<>();
        for (double v : vector) {
            embedding.add(norm == 0 ? 0 : v / norm);
        }

        return embedding;
    }
}
