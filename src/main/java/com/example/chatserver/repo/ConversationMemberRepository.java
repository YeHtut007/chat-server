package com.example.chatserver.repo;

import com.example.chatserver.domain.ConversationMember;
import com.example.chatserver.domain.ConversationMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConversationMemberRepository extends JpaRepository<ConversationMember, ConversationMemberId> {
  boolean existsByConversationIdAndUserId(UUID conversationId, UUID userId);
  Optional<ConversationMember> findByConversationIdAndUserId(UUID conversationId, UUID userId);
}
