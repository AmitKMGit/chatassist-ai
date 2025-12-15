package com.chatassist.backend.util;

import java.util.List;

public class SimilarityUtil {

    public static double cosine(List<Double> v1, List<Double> v2) {
        double dot = 0, norm1 = 0, norm2 = 0;

        for (int i = 0; i < v1.size(); i++) {
            dot += v1.get(i) * v2.get(i);
            norm1 += v1.get(i) * v1.get(i);
            norm2 += v2.get(i) * v2.get(i);
        }

        return dot / (Math.sqrt(norm1) * Math.sqrt(norm2) + 1e-10);
    }
}
