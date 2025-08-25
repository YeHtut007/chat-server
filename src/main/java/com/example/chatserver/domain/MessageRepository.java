package com.example.chatserver.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {
  List<Message> findTop100ByConversationIdOrderBySentAtDesc(UUID conversationId);
  List<Message> findByConversationIdAndSentAtAfterOrderBySentAtAsc(UUID conversationId, Instant after);
}
