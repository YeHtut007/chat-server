package com.example.chatserver.api.dto;

import java.time.Instant;
import java.util.UUID;

public record MessageDto(
    UUID id,
    UUID conversationId,
    String sender,   // map from entity.getSenderUsername()
    String content,
    Instant sentAt
) {}
