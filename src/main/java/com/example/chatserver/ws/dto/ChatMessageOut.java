// src/main/java/com/example/chatserver/ws/dto/ChatMessageOut.java
package com.example.chatserver.ws.dto;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageOut(UUID conversationId, String sender, String content, Instant sentAt) {}
