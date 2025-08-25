package com.example.chatserver.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "messages")
public class Message {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // matches BIGSERIAL
  private Long id;

  @Column(name="conversation_id", nullable=false)
  private UUID conversationId;

  @Column(name="sender_username", nullable=false)
  private String senderUsername;

  @Column(nullable=false) private String content;
  @Column(nullable=false) private String type = "text";
  @Column(name="sent_at", nullable=false) private Instant sentAt;

  @PrePersist void prePersist() { if (sentAt == null) sentAt = Instant.now(); }

  public Message() {}
  public Message(UUID convId, String senderUsername, String content) {
    this.conversationId = convId; this.senderUsername = senderUsername; this.content = content;
  }

  public Long getId() { return id; }                 // <-- Long now
  public UUID getConversationId() { return conversationId; }
  public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }
  public String getSenderUsername() { return senderUsername; }
  public void setSenderUsername(String senderUsername) { this.senderUsername = senderUsername; }
  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public Instant getSentAt() { return sentAt; }
  public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
}

