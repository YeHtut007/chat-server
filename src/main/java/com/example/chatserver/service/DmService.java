package com.example.chatserver.service;

import com.example.chatserver.domain.Conversation;
import com.example.chatserver.domain.ConversationMember;
import com.example.chatserver.repo.ConversationMemberRepository;
import com.example.chatserver.repo.ConversationRepository;
import com.example.chatserver.repo.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DmService {
  private final ConversationRepository conversations;
  private final ConversationMemberRepository members;
  private final UserAccountRepository users;

  public DmService(ConversationRepository conversations,
                   ConversationMemberRepository members,
                   UserAccountRepository users) {
    this.conversations = conversations;
    this.members = members;
    this.users = users;
  }

  private static String norm(String u){ return u == null ? null : u.trim().toLowerCase(); }

  /**
   * Return an existing DM conversation for (a,b) if present; otherwise create one and add both members.
   */
  @Transactional
  public UUID openOrCreateDm(String aUsername, String bUsername) {
    var a = users.findByUsernameIgnoreCase(norm(aUsername)).orElseThrow();
    var b = users.findByUsernameIgnoreCase(norm(bUsername)).orElseThrow();

    if (a.getId().equals(b.getId()))
      throw new IllegalArgumentException("cannot DM yourself");

    // Look through A's conversations and find a DM that also contains B.
    for (var c : conversations.findAllForUser(a.getId())) {
      if (!"DM".equalsIgnoreCase(c.getType())) continue;
      var aMember = members.findByConversationIdAndUserId(c.getId(), a.getId()).orElse(null);
      var bMember = members.findByConversationIdAndUserId(c.getId(), b.getId()).orElse(null);
      if (aMember != null && bMember != null) return c.getId();
    }

    // Create new DM
    var id = UUID.randomUUID();
    var title = a.getDisplayName() + " ↔ " + b.getDisplayName();
    conversations.save(new Conversation(id, "DM", title));
    if (members.findByConversationIdAndUserId(id, a.getId()).isEmpty()) {
      members.save(new ConversationMember(id, a.getId()));
    }
    if (members.findByConversationIdAndUserId(id, b.getId()).isEmpty()) {
      members.save(new ConversationMember(id, b.getId()));
    }
    return id;
  }
}
