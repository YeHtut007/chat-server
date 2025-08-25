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
    var user = users.findByUsername(username).orElse(null);
    return user != null && members.existsByConversationIdAndUserId(conversationId, user.getId());
  }
}
