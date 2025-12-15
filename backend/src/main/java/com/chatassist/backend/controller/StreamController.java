package com.chatassist.backend.controller;

import com.chatassist.backend.service.AiService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequestMapping("/api/stream")
public class StreamController {

    private final AiService aiService;

    public StreamController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/chat")
    public SseEmitter streamChat(@RequestParam("prompt") String prompt) {

        SseEmitter emitter = new SseEmitter(0L); // no timeout

        new Thread(() -> {
            try {
                for (String token : aiService.streamResponse(prompt)) {
                    emitter.send(token);
                }
                emitter.complete();
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();

        return emitter;
    }
}
