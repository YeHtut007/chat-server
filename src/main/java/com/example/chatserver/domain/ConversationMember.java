package com.example.chatserver.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "conversation_member")
@IdClass(ConversationMemberId.class)
public class ConversationMember {
  @Id @Column(name="conversation_id") private UUID conversationId;
  @Id @Column(name="user_id") private UUID userId;

  @Column(nullable=false) private String role = "member";
  @Column(name="joined_at", nullable=false) private Instant joinedAt = Instant.now();
  @Column(name="last_read_at", nullable=false) private Instant lastReadAt = Instant.EPOCH;

  public ConversationMember() {}
  public ConversationMember(UUID conversationId, UUID userId) {
    this.conversationId = conversationId; this.userId = userId;
  }

  public UUID getConversationId() { return conversationId; }
  public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }
  public UUID getUserId() { return userId; }
  public void setUserId(UUID userId) { this.userId = userId; }
  public String getRole() { return role; }
  public void setRole(String role) { this.role = role; }
  public Instant getJoinedAt() { return joinedAt; }
  public void setJoinedAt(Instant joinedAt) { this.joinedAt = joinedAt; }
  public Instant getLastReadAt() { return lastReadAt; }
  public void setLastReadAt(Instant lastReadAt) { this.lastReadAt = lastReadAt; }
}
