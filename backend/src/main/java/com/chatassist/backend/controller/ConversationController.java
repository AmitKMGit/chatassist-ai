package com.chatassist.backend.controller;

import com.chatassist.backend.dto.ConversationDto;
import com.chatassist.backend.dto.MessageDto;
import com.chatassist.backend.dto.MessageRequestDto;
import com.chatassist.backend.model.Conversation;
import com.chatassist.backend.model.Message;
import com.chatassist.backend.model.User;
import com.chatassist.backend.repo.ConversationRepository;
import com.chatassist.backend.repo.MessageRepository;
import com.chatassist.backend.repo.UserRepository;
import com.chatassist.backend.service.ConversationService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConversationService  conversationService;

    public ConversationController(ConversationRepository conversationRepository,
                                  MessageRepository messageRepository,
                                  UserRepository userRepository ,
                                  ConversationService conversationService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.conversationService=conversationService;
    }

    @GetMapping
    public List<ConversationDto> list(@AuthenticationPrincipal UserDetails principal) {
        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow();

        return conversationRepository.findByUserOrderByUpdatedAtDesc(user)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @PostMapping
    public ConversationDto create(@AuthenticationPrincipal UserDetails principal,
                                  @RequestBody(required = false) ConversationDto req) {

        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow();

        Conversation conv = new Conversation();
        conv.setUser(user);
        conv.setTitle(req != null && req.title != null ? req.title : "New Chat");

        conv = conversationRepository.save(conv);
        return toDto(conv);
    }

    @GetMapping("/{id}/messages")
    public List<MessageDto> getMessages(@AuthenticationPrincipal UserDetails principal,
                                        @PathVariable Long id) {

        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow();

        Conversation conv = conversationRepository.findById(id)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow();

        return messageRepository.findByConversationOrderByCreatedAtAsc(conv)
                .stream()
                .map(this::toDto)
                .toList();
    }
    
    @PostMapping("/{id}/messages")
    public List<MessageDto> addMessage(@AuthenticationPrincipal UserDetails principal,
    		                           @PathVariable("id")  Long id,
                                       @RequestBody MessageRequestDto req) {

        User user = userRepository.findByEmail(principal.getUsername())
                .orElseThrow();

        return conversationService.addMessage(id, user, req.content);
    }


    private ConversationDto toDto(Conversation c) {
        ConversationDto dto = new ConversationDto();
        dto.id = c.getId();
        dto.title = c.getTitle();
        dto.createdAt = c.getCreatedAt();
        dto.updatedAt = c.getUpdatedAt();
        return dto;
    }

    private MessageDto toDto(Message m) {
        MessageDto dto = new MessageDto();
        dto.id = m.getId();
        dto.role = m.getRole().name();
        dto.content = m.getContent();
        dto.createdAt = m.getCreatedAt();
        return dto;
    }
}
