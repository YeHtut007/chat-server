package com.example.chatserver.service;

import com.example.chatserver.api.dto.ConversationSummary;
import com.example.chatserver.domain.Message;
import com.example.chatserver.domain.MessageRepository;   // keep this one
import com.example.chatserver.repo.*;                    // your other repos
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ConversationsService {
  private final UserAccountRepository users;
  private final ConversationRepository conversations;
  private final ConversationMemberRepository members;
  private final MessageRepository messages;

  public ConversationsService(
      UserAccountRepository users,
      ConversationRepository conversations,
      ConversationMemberRepository members,
      MessageRepository messages
  ) {
    this.users = users;
    this.conversations = conversations;
    this.members = members;
    this.messages = messages;
  }

  public List<ConversationSummary> listForUser(String username) {
    var user = users.findByUsername(username).orElseThrow();
    var convs = conversations.findAllForUser(user.getId());

    return convs.stream().map(c -> {
      // latest message (Optional<Message>)
      Optional<Message> lastOpt = messages.findTopByConversationIdOrderBySentAtDesc(c.getId());
      Message last = lastOpt.orElse(null);

      // member row to compute unread since lastReadAt
      var member = members.findByConversationIdAndUserId(c.getId(), user.getId()).orElse(null);
      Instant lastReadAt = (member != null) ? member.getLastReadAt() : Instant.EPOCH;
      long unread = messages.countByConversationIdAndSentAtAfter(c.getId(), lastReadAt);

      return new ConversationSummary(
          c.getId(),
          c.getTitle(),
          c.getType(),
          (last != null ? last.getContent() : null),
          (last != null ? last.getSentAt() : null),
          unread
      );
    }).toList();
  }
}
