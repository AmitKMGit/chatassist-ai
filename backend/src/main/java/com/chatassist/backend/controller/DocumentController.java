package com.chatassist.backend.controller;

import com.chatassist.backend.dto.DocumentUploadRequest;
import com.chatassist.backend.model.User;
import com.chatassist.backend.repo.UserRepository;
import com.chatassist.backend.service.DocumentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final UserRepository userRepository;

    public DocumentController(DocumentService documentService,
                              UserRepository userRepository) {
        this.documentService = documentService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public void upload(@AuthenticationPrincipal UserDetails principal,
                       @RequestBody DocumentUploadRequest req) {

        User user = userRepository
                .findByEmail(principal.getUsername())
                .orElseThrow();

        documentService.upload(user, req.name, req.content);
    }
}
