package com.example.chatserver.auth;

import java.util.UUID;

public record UserRecord(UUID id, String username, String displayName, String passwordHash) {}
