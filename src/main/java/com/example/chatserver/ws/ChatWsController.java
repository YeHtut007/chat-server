package com.example.chatserver.ws;

import com.example.chatserver.ws.dto.ChatMessage;
import com.example.chatserver.chat.MessageEntity;
import com.example.chatserver.chat.MessageRepo;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;

@Controller
public class ChatWsController {

  private final SimpMessagingTemplate template;
  private final MessageRepo messages;

  public ChatWsController(SimpMessagingTemplate template, MessageRepo messages) {
    this.template = template;
    this.messages = messages;
  }

  @MessageMapping("/send")
  public void broadcast(ChatMessage msg, Principal principal) {
    String sender = principal.getName(); // from JWT
    var payload = new ChatMessage(msg.conversationId(), sender, msg.content());

    // persist
    messages.save(new MessageEntity(msg.conversationId(), sender, msg.content(), Instant.now()));

    // fan-out
    template.convertAndSend("/topic/conversations." + msg.conversationId(), payload);
  }
}
