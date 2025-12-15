package com.chatassist.backend.repo;

import com.chatassist.backend.model.Message;
import com.chatassist.backend.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);
}
