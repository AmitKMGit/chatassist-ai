package com.chatassist.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
@Service
@Primary
public class AiRouterService implements AiService {

    private final OpenRouterService openRouterService;

    private final String provider;

    public AiRouterService(
            OpenRouterService openRouterService,
            @Value("${ai.provider}") String provider
    ) {
        this.openRouterService = openRouterService;
        this.provider = provider;

        System.out.println("AI PROVIDER LOADED = [" + provider + "]");
    }

    @Override
    public String generateResponse(String prompt) {
        if ("openrouter".equalsIgnoreCase(provider)) {
            return openRouterService.generateResponse(prompt);
        }
        throw new IllegalStateException(
                "AI provider not configured correctly: " + provider
        );
    }
    @Override
    public Iterable<String> streamResponse(String prompt) {
        String full = generateResponse(prompt);
        return List.of(full.split(" "));
    }

}
