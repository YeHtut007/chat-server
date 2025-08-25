package com.example.chatserver.service;

import com.example.chatserver.api.dto.ConversationSummary;
import com.example.chatserver.domain.ConversationMember;
import com.example.chatserver.repo.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConversationsService {
  private final UserAccountRepository users;
  private final ConversationRepository conversations;
  private final ConversationMemberRepository members;
  private final MessageRepository messages;

  public ConversationsService(UserAccountRepository users, ConversationRepository conversations,
                              ConversationMemberRepository members, MessageRepository messages) {
    this.users = users; this.conversations = conversations; this.members = members; this.messages = messages;
  }

  public List<ConversationSummary> listForUser(String username) {
    var user = users.findByUsername(username).orElseThrow();
    var convs = conversations.findAllForUser(user.getId());
    return convs.stream().map(c -> {
      var last = messages.findTopByConversationIdOrderBySentAtDesc(c.getId()).orElse(null);
      var member = members.findByConversationIdAndUserId(c.getId(), user.getId()).orElse(null);
      var lastReadAt = member != null ? member.getLastReadAt() : java.time.Instant.EPOCH;
      long unread = messages.countByConversationIdAndSentAtAfter(c.getId(), lastReadAt);
      return new ConversationSummary(
          c.getId(), c.getTitle(), c.getType(),
          last != null ? last.getContent() : null,
          last != null ? last.getSentAt() : null,
          unread
      );
    }).toList();
  }
}
