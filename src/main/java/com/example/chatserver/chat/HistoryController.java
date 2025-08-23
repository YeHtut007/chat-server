package com.example.chatserver.chat;

import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api/conversations")
public class HistoryController {

  private final MessageRepo repo;

  public HistoryController(MessageRepo repo) { this.repo = repo; }

  // GET last 100 (oldest -> newest). Requires JWT via your existing HTTP security.
  @GetMapping("/{id}/messages")
  public List<MessageEntity> history(@PathVariable UUID id,
                                     @RequestParam(required = false) Long afterEpochMs) {
    if (afterEpochMs != null) {
      return repo.findByConversationIdAndSentAtAfterOrderBySentAtAsc(id, Instant.ofEpochMilli(afterEpochMs));
    }
    var list = repo.findTop100ByConversationIdOrderBySentAtDesc(id);
    Collections.reverse(list); // to return oldest -> newest
    return list;
  }
}
