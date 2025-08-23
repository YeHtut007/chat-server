package com.example.chatserver.chat;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class MessageEntity {
  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private UUID conversationId;

  @Column(nullable = false)
  private String sender;

  @Column(nullable = false, length = 2000)
  private String content;

  @Column(nullable = false)
  private Instant sentAt;

  protected MessageEntity() {} // JPA

  public MessageEntity(UUID conversationId, String sender, String content, Instant sentAt) {
    this.conversationId = conversationId;
    this.sender = sender;
    this.content = content;
    this.sentAt = sentAt;
  }

  public UUID getId() { return id; }
  public UUID getConversationId() { return conversationId; }
  public String getSender() { return sender; }
  public String getContent() { return content; }
  public Instant getSentAt() { return sentAt; }
}
