package com.example.chatserver.api.dto;

import java.util.UUID;

public record UserPublicDto(UUID id, String username, String displayName) {}
