package com.example.chatserver.api.dto;

import java.time.Instant;
import java.util.UUID;

public record MessageDto(
	    Long id,               // <-- was UUID
	    UUID conversationId,
	    String sender,
	    String content,
	    Instant sentAt
	) {}

