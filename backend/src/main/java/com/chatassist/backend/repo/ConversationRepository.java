package com.chatassist.backend.repo;

import com.chatassist.backend.model.Conversation;
import com.chatassist.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByUserOrderByUpdatedAtDesc(User user);
}
