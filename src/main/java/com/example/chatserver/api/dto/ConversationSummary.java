// src/main/java/com/example/chatserver/api/dto/ConversationSummary.java
package com.example.chatserver.api.dto;

import java.time.Instant;
import java.util.UUID;

public record ConversationSummary(
    UUID id, String title, String type,
    String lastMessage, Instant lastAt, long unreadCount
) {}
