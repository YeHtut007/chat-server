package com.example.chatserver.repo;

import com.example.chatserver.domain.Conversation;
import com.example.chatserver.domain.ConversationMember;
import com.example.chatserver.domain.ConversationMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
  @Query("select c from Conversation c where c.id in (" +
         "select m.conversationId from ConversationMember m where m.userId = :userId)")
  List<Conversation> findAllForUser(UUID userId);
}
