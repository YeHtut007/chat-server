package com.example.chatserver.api;

import com.example.chatserver.api.dto.MessageDto;
import com.example.chatserver.service.ChatService;
import com.example.chatserver.service.MembershipService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class MessagesController {
  private final ChatService chat;
  private final MembershipService membership;

  public MessagesController(ChatService chat, MembershipService membership) {
    this.chat = chat; this.membership = membership;
  }

  // GET /api/messages?conversationId=...&before=...&limit=...
  @GetMapping("/messages")
  public List<MessageDto> list(
      @RequestParam UUID conversationId,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant before,
      @RequestParam(defaultValue = "50") int limit,
      Principal principal) {

    if (principal == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
    }
    if (!membership.isMember(principal.getName(), conversationId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member");
    }
    // guard rails on limit
    limit = Math.max(1, Math.min(limit, 200));
    return chat.pageMessages(conversationId, before, limit);
  }

  // POST /api/messages?conversationId=...
  // Body: {"content":"Hello world"}
  @PostMapping("/messages")
  public MessageDto send(
      @RequestParam UUID conversationId,
      @RequestBody Map<String, String> body,
      Principal principal) {

    if (principal == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
    }
    if (!membership.isMember(principal.getName(), conversationId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member");
    }
    String content = body != null ? body.get("content") : null;
    if (content == null || content.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content is required");
    }
    return chat.saveInboundMessage(conversationId, principal.getName(), content.trim());
  }
}
