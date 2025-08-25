package com.example.chatserver.domain;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ConversationMemberId implements Serializable {
  private UUID conversationId;
  private UUID userId;

  public ConversationMemberId() {}
  public ConversationMemberId(UUID conversationId, UUID userId) {
    this.conversationId = conversationId; this.userId = userId;
  }
  @Override public boolean equals(Object o){
    if (this==o) return true; if (!(o instanceof ConversationMemberId m)) return false;
    return Objects.equals(conversationId,m.conversationId) && Objects.equals(userId,m.userId);
  }
  @Override public int hashCode(){ return Objects.hash(conversationId,userId); }
}
