package com.example.chatserver.ws.dto;
import java.util.UUID;
public record ChatMessage(UUID conversationId, String sender, String content) {}
