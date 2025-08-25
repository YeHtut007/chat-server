package com.example.chatserver.api.dto;

import java.time.Instant;
import java.util.UUID;

public record HistoryMessage(UUID conversationId, String sender, String content, Instant sentAt) {}
