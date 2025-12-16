package com.chatassist.backend.service;

import com.chatassist.backend.dto.ConversationDto;
import com.chatassist.backend.dto.MessageDto;
import com.chatassist.backend.model.Conversation;
import com.chatassist.backend.model.DocumentChunk;
import com.chatassist.backend.model.Message;
import com.chatassist.backend.model.User;
import com.chatassist.backend.repo.ConversationRepository;
import com.chatassist.backend.repo.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepo;
    private final MessageRepository messageRepo;
    private final AiService aiService;
    private final RagService ragService;


    public ConversationService(ConversationRepository conversationRepo,
                               MessageRepository messageRepo,
                               AiService aiService,
                               RagService ragService) {
        this.conversationRepo = conversationRepo;
        this.messageRepo = messageRepo;
        this.aiService = aiService;
        this.ragService = ragService;
    }

    public ConversationDto createConversation(User user, String title) {
        Conversation conv = new Conversation();
        conv.setUser(user);
        conv.setTitle(title == null ? "New Chat" : title);

        conv = conversationRepo.save(conv);
        return toDto(conv);
    }

    public List<ConversationDto> list(User user) {
        return conversationRepo.findByUserOrderByUpdatedAtDesc(user)
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<MessageDto> getMessages(Long convId, User user) {
        Conversation conv = conversationRepo.findById(convId)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow();

        return messageRepo.findByConversationOrderByCreatedAtAsc(conv)
                .stream()
                .map(this::toDto)
                .toList();
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
    public List<MessageDto> addMessage(Long conversationId, User user, String content) {

        // 1. Validate and load conversation
        Conversation conv = conversationRepo.findById(conversationId)
                .filter(c -> c.getUser().getId().equals(user.getId()))
                .orElseThrow();

        // 2. Save USER message
        Message userMsg = new Message();
        userMsg.setRole(Message.Role.USER);
        userMsg.setContent(content);
        userMsg.setConversation(conv);
        messageRepo.save(userMsg);

        // 3. Call AI model (placeholder for now)
//        String aiReply = aiService.generateResponse(content);
        List<DocumentChunk> chunks =
                ragService.retrieveRelevantChunks(content);

        String context = chunks.stream()
                .map(DocumentChunk::getContent)
                .collect(Collectors.joining("\n\n"));
        // 4. Save ASSISTANT message
        String prompt =
                "Answer the question using ONLY the context below.\n" +
                "If the answer is not present, say: \"I don't know based on the provided documents.\"\n\n" +
                "Context:\n" + context + "\n\n" +
                "Question:\n" + content;

        String aiReply = aiService.generateResponse(prompt);

        // 5. Update conversation timestamp
        conv.setUpdatedAt(java.time.Instant.now());
        conversationRepo.save(conv);

        // 6. Return full conversation messages
        return messageRepo.findByConversationOrderByCreatedAtAsc(conv)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // Mock AI reply method (will integrate real AI next)
    private String aiChatResponse(String prompt) {
        return "This is AI response to: " + prompt;
    }

}
