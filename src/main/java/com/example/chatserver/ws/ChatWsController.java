package com.example.chatserver.ws;

import com.example.chatserver.api.dto.MessageDto;
import com.example.chatserver.service.ChatService;
import com.example.chatserver.service.MembershipService;
import com.example.chatserver.ws.dto.ChatMessage; // your inbound record: (UUID conversationId, String sender, String content)

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatWsController {
  private final SimpMessagingTemplate broker;
  private final ChatService chatService;
  private final MembershipService membership;

  public ChatWsController(SimpMessagingTemplate broker, ChatService chatService, MembershipService membership) {
    this.broker = broker; this.chatService = chatService; this.membership = membership;
  }

  @MessageMapping("/send") // clients send to /app/send
  public void broadcast(ChatMessage in, Principal principal) {
    var username = principal.getName();
    if (!membership.isMember(username, in.conversationId())) {
      throw new org.springframework.security.access.AccessDeniedException("Not a member");
    }
    // persist then fan out
    MessageDto saved = chatService.saveInboundMessage(in.conversationId(), username, in.content());
    broker.convertAndSend("/topic/chat." + in.conversationId(), saved);
  }
}
