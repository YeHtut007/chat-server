package com.example.chatserver.api;

import com.example.chatserver.api.dto.MessageDto;
import com.example.chatserver.service.ChatService;
import com.example.chatserver.service.MembershipService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MessagesController {
  private final ChatService chat;
  private final MembershipService membership;

  public MessagesController(ChatService chat, MembershipService membership) {
    this.chat = chat; this.membership = membership;
  }

  @GetMapping("/messages")
  public List<MessageDto> list(
      @RequestParam UUID conversationId,
      @RequestParam(required = false) Instant before,
      @RequestParam(defaultValue = "50") int limit,
      Principal principal) {

    if (!membership.isMember(principal.getName(), conversationId)) {
      throw new org.springframework.security.access.AccessDeniedException("Not a member");
    }
    return chat.pageMessages(conversationId, before, limit);
  }
}
