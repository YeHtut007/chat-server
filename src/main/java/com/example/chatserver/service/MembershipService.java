// src/main/java/com/example/chatserver/service/MembershipService.java
package com.example.chatserver.service;

import com.example.chatserver.repo.UserAccountRepository;
import com.example.chatserver.repo.ConversationMemberRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MembershipService {
  private final UserAccountRepository users;
  private final ConversationMemberRepository members;

  public MembershipService(UserAccountRepository users, ConversationMemberRepository members) {
    this.users = users; this.members = members;
  }

  public boolean isMember(String username, UUID conversationId) {
    String uname = username == null ? null : username.trim().toLowerCase(); // normalize
    var user = (uname == null) ? null : users.findByUsernameIgnoreCase(uname).orElse(null);
    boolean ok = user != null && members.existsByConversationIdAndUserId(conversationId, user.getId());
    System.out.printf("[membership] username=%s (norm=%s) dbUserId=%s conv=%s -> %s%n",
        username, uname, user != null ? user.getId() : null, conversationId, ok);
    return ok;
  }
}
