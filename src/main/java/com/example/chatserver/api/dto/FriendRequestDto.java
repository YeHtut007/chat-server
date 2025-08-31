package com.example.chatserver.api.dto;

import java.time.Instant;
import java.util.UUID;

public record FriendRequestDto(UUID id, UserPublicDto from, UserPublicDto to, String status, Instant createdAt) {}
