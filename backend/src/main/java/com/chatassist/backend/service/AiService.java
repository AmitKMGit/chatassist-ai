package com.chatassist.backend.service;

public interface AiService {
	 Iterable<String> streamResponse(String prompt);
    String generateResponse(String prompt);
}
