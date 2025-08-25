package com.example.chatserver.service;

import com.example.chatserver.api.dto.MessageDto;
import com.example.chatserver.domain.Message;
import com.example.chatserver.domain.MessageRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ChatService {
  private final MessageRepository messages;

  public ChatService(MessageRepository messages) {
    this.messages = messages;
  }

  private static MessageDto toDto(Message m) {
    return new MessageDto(
        m.getId(),
        m.getConversationId(),
        m.getSenderUsername(),
        m.getContent(),
        m.getSentAt()
    );
  }

  public MessageDto saveInboundMessage(UUID convId, String senderUsername, String content) {
    Message m = new Message(convId, senderUsername, content);
    m = messages.save(m);
    return toDto(m);
  }

  public List<MessageDto> pageMessages(UUID convId, Instant before, int limit) {
    PageRequest pageReq = PageRequest.of(0, Math.max(1, Math.min(200, limit)));

    Page<Message> page = (before != null)
        ? messages.findByConversationIdAndSentAtBeforeOrderBySentAtDesc(convId, before, pageReq)
        : messages.findByConversationIdOrderBySentAtDesc(convId, pageReq);

    return page.getContent().stream().map(ChatService::toDto).toList();
  }
}
