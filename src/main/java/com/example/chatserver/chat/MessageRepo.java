package com.example.chatserver.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepo extends JpaRepository<MessageEntity, UUID> {
  List<MessageEntity> findTop100ByConversationIdOrderBySentAtDesc(UUID conversationId);
  List<MessageEntity> findByConversationIdAndSentAtAfterOrderBySentAtAsc(UUID conversationId, Instant after);
}
