package com.example.chatserver.chat;

import com.example.chatserver.api.dto.HistoryMessage;
import com.example.chatserver.domain.Message;
import com.example.chatserver.domain.MessageRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/conversations")
public class HistoryController {

  private final MessageRepository repo;

  public HistoryController(MessageRepository repo) {
    this.repo = repo;
  }

  // GET last 100 (oldest -> newest) or everything after a timestamp
  @GetMapping("/{id}/messages")
  public List<HistoryMessage> history(@PathVariable UUID id,
                                      @RequestParam(required = false) Long afterEpochMs) {
    List<Message> list;
    if (afterEpochMs != null) {
      list = repo.findByConversationIdAndSentAtAfterOrderBySentAtAsc(id, Instant.ofEpochMilli(afterEpochMs));
    } else {
      list = repo.findTop100ByConversationIdOrderBySentAtDesc(id);
      Collections.reverse(list); // return oldest -> newest
    }
    return list.stream()
        .map(m -> new HistoryMessage(m.getConversationId(), m.getSenderUsername(), m.getContent(), m.getSentAt()))
        .collect(Collectors.toList());
  }
}
