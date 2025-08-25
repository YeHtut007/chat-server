package com.example.chatserver.repo;

import com.example.chatserver.domain.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {
  Optional<Message> findTopByConversationIdOrderBySentAtDesc(UUID conversationId);
  long countByConversationIdAndSentAtAfter(UUID conversationId, Instant after);
  List<Message> findByConversationIdAndSentAtBeforeOrderBySentAtDesc(UUID conversationId, Instant before, Pageable page);
  List<Message> findByConversationIdOrderBySentAtDesc(UUID conversationId, Pageable page);
}
