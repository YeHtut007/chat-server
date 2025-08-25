package com.example.chatserver.api;

import com.example.chatserver.api.dto.ConversationSummary;
import com.example.chatserver.service.ConversationsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ConversationsController {
  private final ConversationsService service;
  public ConversationsController(ConversationsService service) { this.service = service; }

  @GetMapping("/conversations")
  public List<ConversationSummary> myConversations(Principal principal) {
    return service.listForUser(principal.getName());
  }
}
