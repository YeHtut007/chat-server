package com.example.chatserver.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {
  // you already had these:
  List<Message> findTop100ByConversationIdOrderBySentAtDesc(UUID conversationId);
  List<Message> findByConversationIdAndSentAtAfterOrderBySentAtAsc(UUID conversationId, Instant after);

  // needed by your services:
  Optional<Message> findTopByConversationIdOrderBySentAtDesc(UUID conversationId);

  long countByConversationIdAndSentAtAfter(UUID conversationId, Instant after);

  Page<Message> findByConversationIdOrderBySentAtDesc(UUID conversationId, Pageable pageable);

  Page<Message> findByConversationIdAndSentAtBeforeOrderBySentAtDesc(
      UUID conversationId, Instant before, Pageable pageable);
}
