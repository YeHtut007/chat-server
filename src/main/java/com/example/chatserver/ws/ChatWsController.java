package com.example.chatserver.ws;

import com.example.chatserver.ws.dto.ChatMessage;
import com.example.chatserver.ws.dto.ChatMessageOut;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatWsController {

  private final SimpMessagingTemplate broker;

  public ChatWsController(SimpMessagingTemplate broker) {
    this.broker = broker;
  }

  @MessageMapping("/send")
  public void broadcast(ChatMessage in, Principal principal) {
    var out = new ChatMessageOut(in.conversationId(), principal.getName(), in.content(), java.time.Instant.now());
    broker.convertAndSend("/topic/chat." + in.conversationId(), out);
  }

  
}
