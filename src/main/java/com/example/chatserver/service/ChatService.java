package com.example.chatserver.service;

import com.example.chatserver.api.dto.MessageDto;
import com.example.chatserver.domain.Message;
import com.example.chatserver.repo.MessageRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {
  private final MessageRepository messages;

  public ChatService(MessageRepository messages) { this.messages = messages; }

  public MessageDto saveInboundMessage(UUID convId, String senderUsername, String content) {
    var m = new Message(convId, senderUsername, content);
    m = messages.save(m);
    return new MessageDto(m.getId(), convId, senderUsername, content, m.getSentAt());
  }

  public List<MessageDto> pageMessages(UUID convId, Instant before, int limit) {
    var page = PageRequest.of(0, Math.max(1, Math.min(200, limit)));
    var list = (before != null)
        ? messages.findByConversationIdAndSentAtBeforeOrderBySentAtDesc(convId, before, page)
        : messages.findByConversationIdOrderBySentAtDesc(convId, page);
    return list.stream()
        .map(m -> new MessageDto(m.getId(), m.getConversationId(), m.getSenderUsername(), m.getContent(), m.getSentAt()))
        .toList();
  }
}
